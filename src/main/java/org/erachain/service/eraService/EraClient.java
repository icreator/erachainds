package org.erachain.service.eraService;

import org.erachain.api.chain.SendTX;
import org.erachain.entities.account.Account;
import org.erachain.entities.datainfo.DataEra;
import org.erachain.entities.datainfo.DataInfo;
import org.erachain.repositories.DbUtils;
import org.erachain.service.JsonService;
import org.erachain.service.RestClient;
import org.erachain.utils.crypto.Base58;
import org.erachain.utils.crypto.Pair;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@PropertySource("classpath:custom.properties")
public class EraClient {
    @Autowired
    private Logger logger;

    @Value("${EraService_Url}")
    private String EraService_Url;

    @Value("${EraService_creator}")
    private String EraService_creator;


//    @Value("${EraService_Url_Signature}")
    private String ERASERVICE_URL_SIGNATURE;

//    @Value("${ERASERVICE_URL_API}")
    private String ERASERVICE_URL_API;

    @Value("#{'${ERA_SERVICE_IPS}'.trim().replaceAll(\"\\s*(?=,)|(?<=,)\\s*\", \"\").split(',')}")
    private List<String> ERA_SERVICE_IPS;

    @Value("${EraService_password}")
    private String EraService_password;

    @Value("${EraService_title}")
    private String EraService_title;

    @Autowired
    private JsonService jsonService;

    @Autowired
    private DbUtils dbUtils;

    @Value("${TRANS_MAXSIZE}")
    private int TRANS_MAXSIZE;


    @Value("${ENCRYPT}")
    private boolean ENCRYPT;

    private RestClient restClient;

    @Autowired
    public EraClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public void setSignature(DataInfo dataInfo, Account account, String signiture, int offset) throws Exception {

        List<DataEra> dataEras = new ArrayList<>();
        for (byte[] dt : dataInfo.getData(dbUtils, TRANS_MAXSIZE)) {
            String data = new String(dt, "UTF8");
            logger.info(" data to client " + data);
            DataEra dataEra = new DataEra();
            dataEra.setDataInfoId(dataInfo.getId());
            if (signiture != null) {
                dataEra.setSignature(signiture);
                dataEra.setOffset(offset);
                dataEra.setLengh(dataInfo.getData().length);
            } else {
                dataEra.setSignature(getSignForData(account, data));
            }
            dataEras.add(dataEra);
        }
        int partNo = 0;
        for (DataEra dataEra : dataEras) {
            dataEra.setPartNo(partNo++);
            dbUtils.setDbObj(dataEra, "DataEra");
        }
    }

    public String getSignForData(Account account, String data) throws Exception {
        logger.info("Sending by API...");
        String result = null;
        SendTX tx;
        try {
            String privateKeyCreator = account.getPrivateKey();
            String publicKeyCreator = account.getPublicKey();
            String recipient = account.getRecipient();
            String publicKeyRecipient = account.getPublicKeyRecipient();
            byte encrypt = ENCRYPT ? (byte) 1 : (byte) 0;
            tx = new SendTX(publicKeyCreator, privateKeyCreator, recipient, publicKeyRecipient,
                    EraService_title, data,
                    BigDecimal.valueOf(0),
                    System.currentTimeMillis(), 0, (byte) 0, encrypt);
            tx.sign(new Pair<>(Base58.decode(privateKeyCreator), Base58.decode(publicKeyCreator)));
            String byteCode = Base58.encode(tx.toBytes(true));
            for (String ip : ERA_SERVICE_IPS) {
                try {
                    //todo Gleb поискать и посмотреть библиотеку для формирования url
                    //todo Gleb реализовать механизм что ответ получается по любому другому доступному url
                    // кроме того, по которому отправлятся
                    ERASERVICE_URL_API = "http://" + ip + ":9067/api/broadcast";
                    ERASERVICE_URL_SIGNATURE = "http://" + ip + ":9067/apirecords/get";
                    result = restClient.getResult(ERASERVICE_URL_API + "/" + byteCode);
                    break;
                } catch (Exception e) {
                    logger.error("Request by url: " + ERASERVICE_URL_API + " can't be processed", e);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new Exception(ERASERVICE_URL_API + " " + e.getMessage());
        }
        if (result == null) {
            logger.info("All era services are not reachable");
            throw new Exception("All era services are not reachable");
        }
        String status;
        try {
            status = jsonService.getValue(result, "status");
        } catch (Exception e) {
            logger.error("Doesn't find field 'status' in json response, received from blockchain after send data");
            String message = jsonService.getValue(result, "message");
            logger.error("message = " + message);
            throw new Exception(e.getMessage());
        }
        if (!status.equals("ok")) {
            logger.error(ERASERVICE_URL_API + " status = " + status);
            throw new Exception(ERASERVICE_URL_API + " status = " + status);
        }
        String signature = Base58.encode(tx.getSignature());
        logger.info(" Acquire signature for " + account.getId() + " : " + signature);
        return signature;
    }

    public String checkChain(DataEra dataEra) throws Exception {
        String signature = dataEra.getSignature();
        String result;
        try {
            result = restClient.getResult(ERASERVICE_URL_SIGNATURE + "/" + signature);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new Exception(ERASERVICE_URL_SIGNATURE + " " + e.getMessage());
        }
        return result;
    }
}
