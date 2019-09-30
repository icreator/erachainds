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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@PropertySource("classpath:custom.properties")
public class EraClient {
    @Autowired
    private Logger logger;

    @Value("${EraService_Url}")
    private String EraService_Url;

    @Value("${EraService_creator}")
    private String EraService_creator;


    @Value("${EraService_Url_Signature}")
    private String EraService_Url_Signature;

    @Value("${ERASERVICE_URL_API}")
    private String ERASERVICE_URL_API;

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
        String result;
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
            result = restClient.getResult(ERASERVICE_URL_API + "/" + byteCode);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new Exception(EraService_Url + " " + e.getMessage());
        }
        String status = jsonService.getValue(result, "status");
        if (!status.equals("ok")) {
            logger.error(EraService_Url + " status = " + status);
            throw new Exception(EraService_Url + " status = " + status);
        }
        String signature = Base58.encode(tx.getSignature());
        logger.info(" Acquire signature for " + account.getId() + " : " + signature);
        return signature;
    }

    public String checkChain(DataEra dataEra) throws Exception {
        String signature = dataEra.getSignature();
        String result = null;
        try {
            result = restClient.getResult(EraService_Url_Signature + "/" + signature);
        } catch (Exception e) {
            //   logger.error(e.getMessage());
            throw new Exception(EraService_Url_Signature + " " + e.getMessage());

        }
        return result;
    }
}
