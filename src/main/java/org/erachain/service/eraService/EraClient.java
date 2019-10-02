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
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@PropertySource("classpath:custom.properties")
public class EraClient {
    @Autowired
    private Logger logger;

    @Value("${EraService_creator}")
    private String EraService_creator;

    private String ERASERVICE_URL_SIGNATURE;

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

    private String ERASERVICE_URL_IP_RESPONSE_EXCEPT;
    private boolean FLAG_RECEIVING_IP = true;
    private boolean FLAG_RECEIVING_IP_CHECK = true;

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
            if (!FLAG_RECEIVING_IP) {
                logger.info("Not receiving ip, already remembered");
                try {
                    result = restClient.getResult(ERASERVICE_URL_API + "/" + byteCode);
                    logger.info("Send successful data to blockchain with remembered ip = " + ERASERVICE_URL_API);
                } catch (ResourceAccessException e) {
                    logger.warn("Request by remembered url: " + ERASERVICE_URL_API + " can't be processed");
                    logger.warn("Switch to search ip");
                    FLAG_RECEIVING_IP = true;
                }
            }
            if (FLAG_RECEIVING_IP) {
                logger.info("Receiving ip");
                for (int i = 0; i < ERA_SERVICE_IPS.size(); i++) {
                    String ip = ERA_SERVICE_IPS.get(i);
                    try {
                        URL url = new URL("http", ip, 9067, "api/broadcast");
                        ERASERVICE_URL_API = url.toString();
                        result = restClient.getResult(ERASERVICE_URL_API + "/" + byteCode);
                        logger.info("Send successful data to blockchain with ip = " + ip);
                        ERASERVICE_URL_IP_RESPONSE_EXCEPT = ip;
                        FLAG_RECEIVING_IP = false;
                        FLAG_RECEIVING_IP_CHECK = true;
                        break;
                    } catch (ResourceAccessException e) {
                        logger.warn("Request by url: " + ERASERVICE_URL_API + " can't be processed");
                        if (i == ERA_SERVICE_IPS.size() - 1) {
                            throw new Exception("No one ip from list is reachable");
                        }
                    }
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
        logger.info("Checking chain...");
        String signature = dataEra.getSignature();
        String result = null;
        try {
            if (!FLAG_RECEIVING_IP_CHECK) {
                logger.info("Not receiving ip, already remembered");
                try {
                    result = restClient.getResult(ERASERVICE_URL_SIGNATURE + "/" + signature);
                    logger.info("check successful data from blockchain with remembered ip = " + ERASERVICE_URL_SIGNATURE);
                } catch (ResourceAccessException e) {
                    logger.warn("Request by remembered url: " + ERASERVICE_URL_SIGNATURE + " can't be processed");
                    logger.warn("Switch to search ip");
                    FLAG_RECEIVING_IP_CHECK = true;
                }
            }
            if (FLAG_RECEIVING_IP_CHECK) {
                for (int i = 0; i < ERA_SERVICE_IPS.size(); i++) {
                    String ip = ERA_SERVICE_IPS.get(i);
                    if (i == ERA_SERVICE_IPS.size() - 1 && ip.equals(ERASERVICE_URL_IP_RESPONSE_EXCEPT)) {
                        throw new Exception("No one ip from list is reachable for checking by signature");
                    }
                    if (ip.equals(ERASERVICE_URL_IP_RESPONSE_EXCEPT)) {
                        continue;
                    }
                    try {
                        URL url = new URL("http", ip, 9067, "apirecords/get");
                        ERASERVICE_URL_SIGNATURE = url.toString();
                        result = restClient.getResult(ERASERVICE_URL_SIGNATURE + "/" + signature);
                        logger.info("successful checked data");
                        FLAG_RECEIVING_IP_CHECK = false;
                        break;
                    } catch (ResourceAccessException e) {
                        logger.warn("Request signature by url: " + ERASERVICE_URL_SIGNATURE + " can't be processed");
                        if (i == ERA_SERVICE_IPS.size() - 1) {
                            throw new Exception("No one ip from list is reachable for checking by signature");
                        }
                    }
                }
            }
            } catch(Exception e){
                logger.error(e.getMessage(), e);
                throw new Exception(ERASERVICE_URL_SIGNATURE + " " + e.getMessage());
            }
            return result;
        }
    }
