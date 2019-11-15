package org.erachain.service.eraService;

import org.apache.flink.api.common.typeutils.base.LongComparator;
import org.apache.flink.api.java.tuple.Tuple2;
import org.erachain.api.chain.SendTX;
import org.erachain.entities.account.Account;
import org.erachain.entities.account.AccountSender;
import org.erachain.entities.datainfo.DataEra;
import org.erachain.entities.datainfo.DataInfo;
import org.erachain.repositories.AccountSendersDAO;
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

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@PropertySource("classpath:custom.properties")
public class EraClient {

    private final Logger logger;

    @Autowired
    private AccountSendersDAO accountSenders;

    @Value("${EraService_creator}")
    private String EraService_creator;

    private String ERASERVICE_URL_SIGNATURE;

    private String ERASERVICE_URL_API;

    @Value("#{'${ERA_SERVICE_IPS}'.trim().replaceAll(\"\\s*(?=,)|(?<=,)\\s*\", \"\").split(',')}")
    private List<String> ERA_SERVICE_IPS;

    private List<Tuple2<String, Long>> ERA_SERVICE_IPS_RANGE;

    @Value("${EraService_password}")
    private String EraService_password;

    @Value("${EraService_title}")
    private String EraService_title;

    @Autowired
    private JsonService jsonService;

    @Autowired
    private DbUtils dbUtils;

    @Value("${TRANSACTION_MAXSIZE_BYTE}")
    private int TRANSACTION_MAXSIZE_BYTE;

    @Value("${ENCRYPT}")
    private boolean ENCRYPT;

    private RestClient restClient;

    private String ERASERVICE_URL_IP_RESPONSE_EXCEPT;

    private boolean FLAG_RECEIVING_IP = true;
    
    private boolean FLAG_RECEIVING_IP_CHECK = true;

    @Value("${BLOCKCHAIN_PORT}")
    private int BLOCKCHAIN_PORT;

    @Autowired
    public EraClient(RestClient restClient, Logger logger) {
        this.restClient = restClient;
        this.logger = logger;
    }

    @PostConstruct
    public void postConstructInit() {
        ERA_SERVICE_IPS_RANGE = ERA_SERVICE_IPS.stream().map((ip) -> Tuple2.of(ip, 0L)).collect(Collectors.toList());
    }

    public void saveToBlockchainAndWriteDataEraInDb(DataInfo dataInfo, Account account, String signature, int offset) throws Exception {
        List<DataEra> dataEras = new ArrayList<>();
        for (byte[] dt : dataInfo.getDataSplit(TRANSACTION_MAXSIZE_BYTE)) {
            String data = new String(dt, StandardCharsets.UTF_8);
            logger.debug("Data to client " + data);
            DataEra dataEra = new DataEra();
            dataEra.setDataInfoId(dataInfo.getId());
            if (signature != null) {
                dataEra.setSignature(signature);
                dataEra.setOffset(offset);
                dataEra.setLength(dataInfo.getData().length);
            } else {
                dataEra.setSignature(sendingToBlockchain(account, data));
            }
            dataEras.add(dataEra);
        }
        int partNo = 0;
        for (DataEra dataEra : dataEras) {
            dataEra.setPartNo(partNo++);
            dbUtils.setDbObj(dataEra, "DataEra");
        }
    }

    public String sendingToBlockchain(Account account, String data) throws Exception {
        logger.debug("Sending by API to blockchain...");
        String result = null;
        SendTX tx;
        try {
            AccountSender accountSender = accountSenders.getAccountSenderById(account.getIdSender());
            String privateKeyCreator = accountSender.getSenderPrivateKey();
            String publicKeyCreator = accountSender.getSenderPublicKey();
            String recipient = account.getAccountRecipient();
            String publicKeyRecipient = account.getRecipientPublicKey();
            byte encrypt = ENCRYPT ? (byte) 1 : (byte) 0;
            tx = new SendTX(publicKeyCreator, privateKeyCreator, recipient, publicKeyRecipient,
                    EraService_title, data,
                    BigDecimal.valueOf(0),
                    System.currentTimeMillis(), 0, (byte) 0, encrypt);
            tx.sign(new Pair<>(Base58.decode(privateKeyCreator), Base58.decode(publicKeyCreator)));
            String byteCode = Base58.encode(tx.toBytes(true));
            if (!FLAG_RECEIVING_IP) {
                logger.debug("Not checking ip, already remembered");
                try {
                    result = restClient.getResult(ERASERVICE_URL_API + "/" + byteCode);
                    logger.debug("Send successful data to blockchain with remembered ip = " + ERASERVICE_URL_API);
                } catch (ResourceAccessException e) {
                    logger.debug("Request by remembered url: " + ERASERVICE_URL_API + " can't be processed");
                    logger.debug("Switch to search ip");
                    FLAG_RECEIVING_IP = true;
                }
            }
            if (FLAG_RECEIVING_IP) {
                logger.debug("Checking ips...");
                ERA_SERVICE_IPS_RANGE = ERA_SERVICE_IPS_RANGE.stream().sorted(
                        (tuple1, tuple2) -> new LongComparator(true).compare(tuple1.f1, tuple2.f1)).
                        collect(Collectors.toList());
                for (int i = 0; i < ERA_SERVICE_IPS_RANGE.size(); i++) {
                    String ip = ERA_SERVICE_IPS_RANGE.get(i).f0;
                    try {
                        URL url = new URL("http", ip, BLOCKCHAIN_PORT, "/api/broadcast");
                        ERASERVICE_URL_API = url.toString();
                        result = restClient.getResult(ERASERVICE_URL_API + "/" + byteCode);
                        logger.debug("Send successful data to blockchain with ip = " + ip);
                        ERASERVICE_URL_IP_RESPONSE_EXCEPT = ip;
                        FLAG_RECEIVING_IP = false;
                        FLAG_RECEIVING_IP_CHECK = true;
                        break;
                    } catch (ResourceAccessException e) {
                        logger.debug("Request by url: " + ERASERVICE_URL_API + " can't be processed");
                        ERA_SERVICE_IPS_RANGE.get(i).f1++;
                        if (i == ERA_SERVICE_IPS_RANGE.size() - 1) {
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
            logger.debug("All era services are not reachable");
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
        logger.debug("Acquire signature for " + account.getId() + " : " + signature);
        return signature;
    }

    public String checkChain(DataEra dataEra) throws Exception {
        logger.debug("Checking chain...");
        String signature = dataEra.getSignature();
        String result = null;
        try {
            if (!FLAG_RECEIVING_IP_CHECK) {
                logger.debug("Not checking ip check signature, already remembered");
                try {
                    result = restClient.getResult(ERASERVICE_URL_SIGNATURE + "/" + signature);
                    logger.debug("check successful data from blockchain with remembered ip = " + ERASERVICE_URL_SIGNATURE);
                } catch (ResourceAccessException e) {
                    logger.debug("Request check by remembered url: " + ERASERVICE_URL_SIGNATURE + " can't be processed");
                    logger.debug("Switch to search ip check");
                    FLAG_RECEIVING_IP_CHECK = true;
                }
            }
            if (FLAG_RECEIVING_IP_CHECK) {
                logger.debug("Checking ips (signature)...");
                ERA_SERVICE_IPS_RANGE = ERA_SERVICE_IPS_RANGE.stream().sorted(
                        (tuple1, tuple2) -> new LongComparator(true).compare(tuple1.f1, tuple2.f1)).
                        collect(Collectors.toList());
                for (int i = 0; i < ERA_SERVICE_IPS_RANGE.size(); i++) {
                    String ip = ERA_SERVICE_IPS_RANGE.get(i).f0;
                    if (i == ERA_SERVICE_IPS_RANGE.size() - 1 && ip.equals(ERASERVICE_URL_IP_RESPONSE_EXCEPT)) {
                        result = checkIp(signature, ERASERVICE_URL_IP_RESPONSE_EXCEPT, "Checked by same ip than send");
                        break;
                    }
                    if (ip.equals(ERASERVICE_URL_IP_RESPONSE_EXCEPT)) {
                        continue;
                    }
                    try {
                        result = checkIp(signature, ip, "successful checked data");
                        break;
                    } catch (ResourceAccessException e) {
                        logger.debug("Request signature by url: " + ERASERVICE_URL_SIGNATURE + " can't be processed");
                        ERA_SERVICE_IPS_RANGE.get(i).f1++;
                        if (i == ERA_SERVICE_IPS_RANGE.size() - 1) {
                            try {
                                result = checkIp(signature, ERASERVICE_URL_IP_RESPONSE_EXCEPT, "Checked by same ip than send");
                            } catch (ResourceAccessException exception){
                                throw new Exception("No one ip from list is reachable for checking by signature");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new Exception(ERASERVICE_URL_SIGNATURE + " " + e.getMessage());
        }
        return result;
    }

    private String checkIp(String signature, String eraservice_url_ip_response_except, String s) throws Exception {
        String result;
        URL url = new URL("http", eraservice_url_ip_response_except, BLOCKCHAIN_PORT, "/apirecords/get");
        ERASERVICE_URL_SIGNATURE = url.toString();
        result = restClient.getResult(ERASERVICE_URL_SIGNATURE + "/" + signature);
        logger.debug(s);
        FLAG_RECEIVING_IP_CHECK = false;
        return result;
    }
}
