package org.erachain.process;

import org.apache.commons.lang3.time.DateUtils;
import org.erachain.entities.ActiveJob;
import org.erachain.entities.JobState;
import org.erachain.entities.account.Account;
import org.erachain.entities.datainfo.DataEra;
import org.erachain.entities.datainfo.DataInfo;
import org.erachain.entities.request.ActParams;
import org.erachain.entities.request.Request;
import org.erachain.repositories.AccountProc;
import org.erachain.repositories.DbUtils;
import org.erachain.repositories.InfoSave;
import org.erachain.service.ServiceFactory;
import org.erachain.service.ServiceInterface;
import org.erachain.service.energetik.JsonService;
import org.erachain.service.eraService.EraClient;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@PropertySource("classpath:custom.properties")
public class ServiceMonitor {

    @Autowired
    private Logger logger;

    @Autowired
    private AccountProc accountProc;

    @Autowired
    private InfoSave infoSave;

    @Autowired
    private ServiceFactory serviceFactory;

    @Autowired
    private EraClient eraClient;

    @Autowired
    private JsonService jsonService;

    @Autowired
    private DbUtils dbUtils;

    @Value("${Service_Url}")
    private String Service_Url;

    @Value("${FETCH_DATA_AFTER_RUN}")
    private String FETCH_DATA_AFTER_RUN;

//    @Value("${CHECK_DATA_AFTER_RUN}")
//    private String CHECK_DATA_AFTER_RUN;

    @Value("${FETCH_DATA_AFTER_SUBMIT}")
    private String FETCH_DATA_AFTER_SUBMIT;

//    @Value("${CHECK_DATA_AFTER_SUBMIT}")
//    private String CHECK_DATA_AFTER_SUBMIT;

    @Value("${FETCH_DATA_AFTER_ACCEPT}")
    private String FETCH_DATA_AFTER_ACCEPT;

//    @Value("${CHECK_DATA_AFTER_ACCEPT}")
//    private String CHECK_DATA_AFTER_ACCEPT;

    @Value("${FETCH_DATA_AFTER_SEND_TO_CLIENT}")
    private String FETCH_DATA_AFTER_SEND_TO_CLIENT;

//    @Value("${CHECK_DATA_AFTER_SEND_TO_CLIENT}")
//    private String CHECK_DATA_AFTER_SEND_TO_CLIENT;


    public void checkAccounts() {
        List<Account> accounts = accountProc.getAccounts();
        accounts.forEach(account -> {
            checkAccount(account);
        });
    }
    public void checkAccount(int accountId) {
        checkAccount(accountProc.getAccountById(accountId));
    }
    public void checkAccount(Account account) {
        List<Request> requests = accountProc.getRequests(account.getId());
        requests.forEach(request -> {
            if (request.checkTime()) {
                request.setLastReceived();
                Map<String, String> params = account.getParams();
                logger.info(" params " + params.get(Service_Url));
                ServiceInterface service = serviceFactory.getService(params.get(Service_Url));
                logger.info(" account service " + service);
                List<String> idents = service.getIdentityList(params);
                List<DataInfo> dataInfos = new ArrayList<>();
                idents.forEach(ident -> {
                    params.put(account.getIdentityName(), ident);
                    request.setParams(params, dbUtils);
                    String json = service.getIdentityValues(params);
                    if (json != null && !json.isEmpty()) {
                        byte[] data = json.getBytes();
                        DataInfo dataInfo = new DataInfo();
                        dataInfo.setRunDate(new Timestamp(System.currentTimeMillis()));
                        dataInfo.setAccountId(account.getId());
                        dataInfo.setIdentity(ident);
                        dataInfo.setData(data);
                        try {
                            dataInfos.add(dataInfo);
                            request.addlastReceived();
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                    }
                });
                if (request.getLastReceived() > 0) {
                    try {
                        logger.info(" set actRequestId ");
                        int actRequestId = request.setResult(params, dbUtils);
                        logger.info(" actRequestId " + actRequestId);
                        accountProc.afterRun(request);
                        dataInfos.forEach(dataInfo -> {
                            try {
                                dataInfo.setActRequestId(actRequestId);
                                logger.info("data saved  for ident " + dataInfo.getIdentity() + " " + dataInfo.getRunDate());
                                infoSave.saveData(dataInfo);
                            } catch (SQLException e) {
                                logger.info(e.getMessage());
                            }
                        });
                        logger.info(" account updated after run " + account.getUser());
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        });
    }

    public void checkDataSubmit() {
        List<DataInfo> dataInfos = infoSave.fetchData(FETCH_DATA_AFTER_RUN);

        dataInfos.forEach(dataInfo -> {
            if (dataInfo.getRunDate() != null && dataInfo.getSubDate() == null) {
                Account account = accountProc.getAccountById(dataInfo.getAccountId());
                try {
                    eraClient.setSignature(dataInfo, account);
                } catch (SQLException e) {
                    logger.error(e.getMessage());                }
                try {
                    infoSave.afterSubmit(dataInfo);
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
        });
    }

    public void checkDataAccept() {
        List<DataInfo> dataInfos = infoSave.fetchData(FETCH_DATA_AFTER_SUBMIT);

        dataInfos.forEach(dataInfo -> {
            if (dataInfo.getSubDate() != null && dataInfo.getAccDate() == null) {
                dataInfo.getDataEras(dbUtils).forEach(dataEra -> {
                    int confirmations = 0;
                    String result = eraClient.checkChain(dataEra);
                    confirmations = jsonService.getValue(result, "confirmations");

                    if (confirmations == 0) {
                        logger.info(" unconfirmed " + dataEra.getSignature());
                    } else {
                        logger.info(" confirmed " + dataEra.getSignature());
                        int height = jsonService.getValue(result, "height");
                        int sequence = jsonService.getValue(result, "sequence");
                        dataEra.setBlockTrId(height + "-" + sequence);
                    }
                });
                try {
                    infoSave.afterAccept(dataInfo);
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
        });
    }

    public void checkSendToClient() {
        List<DataInfo> dataInfos = infoSave.fetchData(FETCH_DATA_AFTER_ACCEPT);

        dataInfos.forEach(dataInfo -> {
            if (dataInfo.getAccDate() != null && dataInfo.getSendToClientDate() == null) {
                Account account = accountProc.getAccountById(dataInfo.getAccountId());
                Map<String, String> params = account.getParams();
                logger.info(" params " + params.get(Service_Url));
                ServiceInterface service = serviceFactory.getService(params.get(Service_Url));
                logger.info(" account service " + service);
                params.put(account.getIdentityName(), dataInfo.getIdentity());
                DataEra dataEra = dbUtils.fetchData(DataEra.class, "DataEra", "dataInfoId = " + dataInfo.getId()).get(0);
                params.put("transaction", dataEra.getBlockTrId());
                List<ActParams> actParams = dbUtils.fetchData(ActParams.class, "ActParams", " actRequestId = " + dataInfo.getActRequestId());
                actParams.forEach(actParam -> {
                    params.put(actParam.getParamName(), actParam.getParamValue());
                });
                service.setIdentityValues(params);
                try {
                    infoSave.afterSendToClient(dataInfo);
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
        });
    }

    public void checkAcceptedByClient() {
        List<DataInfo> dataInfos = infoSave.fetchData(FETCH_DATA_AFTER_SEND_TO_CLIENT);

        dataInfos.forEach(dataInfo -> {
            if (dataInfo.getSendToClientDate() != null && dataInfo.getAcceptClientDate() == null) {
                Account account = accountProc.getAccountById(dataInfo.getAccountId());
                Map<String, String> params = account.getParams();
                logger.info(" params " + params.get(Service_Url));
                ServiceInterface service = serviceFactory.getService(params.get(Service_Url));
                logger.info(" account service " + service);
                params.put(account.getIdentityName(), dataInfo.getIdentity());
                List<ActParams> actParams = dbUtils.fetchData(ActParams.class, "ActParams", " actRequestId = " + dataInfo.getActRequestId());
                actParams.forEach(actParam -> {
                    params.put(actParam.getParamName(), actParam.getParamValue());
                });
                if (!service.checkIdentityValues(params))
                    return;
                try {
                    infoSave.afterAcceptedByClient(dataInfo);
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
        });
    }


}
