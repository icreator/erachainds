package org.erachain.process;


import org.erachain.entities.account.Account;
import org.erachain.entities.datainfo.DataEra;
import org.erachain.entities.datainfo.DataInfo;
import org.erachain.entities.request.ActParams;
import org.erachain.repositories.AccountProc;
import org.erachain.repositories.DbUtils;
import org.erachain.repositories.InfoSave;
import org.erachain.service.ServiceFactory;
import org.erachain.service.ServiceInterface;
import org.erachain.service.JsonService;
import org.erachain.service.eraService.EraClient;
import org.erachain.utils.DateUtl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

import java.util.*;

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

    @Autowired
    private DateUtl dateUtl;

    @Value("${Service_Url}")
    private String Service_Url;

    @Value("${TRANS_MINSIZE}")
    private int TRANS_MINSIZE;



    @Value("${FETCH_DATA_FOR_SUBMIT}")
    private String FETCH_DATA_FOR_SUBMIT;

    @Value("${FETCH_DATA_AFTER_SUBMIT}")
    private String FETCH_DATA_AFTER_SUBMIT;



    @Value("${FETCH_DATA_AFTER_ACCEPT}")
    private String FETCH_DATA_AFTER_ACCEPT;


    @Value("${FETCH_DATA_AFTER_SEND_TO_CLIENT}")
    private String FETCH_DATA_AFTER_SEND_TO_CLIENT;


    public void checkDataSubmit()throws Exception {
        List<DataInfo> dataInfos = infoSave.fetchData(FETCH_DATA_FOR_SUBMIT.replace("?", Long.toString(new Date().getTime())));
        if (dataInfos == null || dataInfos.isEmpty()) {
            return;
        }
        String signature = null;
        int accountId = dataInfos.get(0).getAccountId();
        Account account = accountProc.getAccountById(accountId);
        if(getSize(dataInfos) < TRANS_MINSIZE) {
            String data = getData(dataInfos);
            signature = eraClient.getSignForData(account, data);
        }
        int offset = 0;
        for (DataInfo dataInfo : dataInfos) {
            if (dataInfo.getRunDate() != null && dataInfo.getSubDate() == null && dataInfo.getAccountId() == accountId) {
                try {
                    eraClient.setSignature(dataInfo, account, signature, offset);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    throw e;
                }
                try {
                    infoSave.afterSubmit(dataInfo);
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                    throw e;
                }
                offset += dataInfo.getData().length;
            }

        }
    }
    private int getSize(List<DataInfo> dataInfos) {
        int size = 0;
        for (DataInfo dataInfo : dataInfos) {
            size += dataInfo.getData().length;
        }
        return size;
    }

    private String getData(List<DataInfo> dataInfos) {
        int size = 0;
        StringBuffer stringBuffer = new StringBuffer("");
        for (DataInfo dataInfo : dataInfos) {
           stringBuffer.append(dataInfo.getData());
        }
        return stringBuffer.toString();
    }
    public void checkDataAccept() throws Exception {
        List<DataInfo> dataInfos = infoSave.fetchData(FETCH_DATA_AFTER_SUBMIT);

        for (DataInfo dataInfo : dataInfos) {
            if (dataInfo.getSubDate() != null && dataInfo.getAccDate() == null) {
                int confirmed = 0;
                int unConfirmed = 0;
                for (DataEra dataEra : dataInfo.getDataEras(dbUtils)) {
                    int confirmations = 0;
                    String result = null;
                    try {
                        result = eraClient.checkChain(dataEra);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        throw e;
                    }
                    confirmations = jsonService.getValue(result, "confirmations");

                    if (confirmations == 0) {
                        logger.debug(" unconfirmed " + dataEra.getSignature());
                        unConfirmed ++;
                    } else {
                        logger.info(" confirmed " + dataEra.getSignature());
                        int height = jsonService.getValue(result, "height");
                        int sequence = jsonService.getValue(result, "sequence");
                        dataEra.setBlockTrId(height + "-" + sequence);
                        logger.info(" save block and trans for dataEra " + dataEra.getBlockTrId());
                        infoSave.afterAcceptEra(dataEra);
                        logger.info(" save block and trans for dataEra " + dataEra.getId());
                        confirmed ++;
                    }
                }
                try {
                    if (unConfirmed == 0 && confirmed > 0)
                        infoSave.afterAccept(dataInfo);
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    public void checkSendToClient() {
        List<DataInfo> dataInfos = infoSave.fetchData(FETCH_DATA_AFTER_ACCEPT);

        for (DataInfo dataInfo : dataInfos) {
            if (dataInfo.getAccDate() != null && dataInfo.getSendToClientDate() == null) {
                Account account = accountProc.getAccountById(dataInfo.getAccountId());
                Map<String, String> params = account.getParams();
                logger.debug(" params " + params.get(Service_Url));
                ServiceInterface service = serviceFactory.getService(params.get(Service_Url));
                logger.debug(" account service " + service);
                params.put(account.getIdentityName(), dataInfo.getIdentity());
                DataEra dataEra = dbUtils.fetchData(DataEra.class, "DataEra", "dataInfoId = " + dataInfo.getId()).get(0);
                params.put("transaction", dataEra.getBlockTrId());
                List<ActParams> actParams = dbUtils.fetchData(ActParams.class, "ActParams", " actRequestId = " + dataInfo.getActRequestId());
                actParams.forEach(actParam -> {
                    params.put(actParam.getParamName(), actParam.getParamValue());
                });
                try {
                    service.setIdentityValues(params);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    continue;
                }
                try {
                    infoSave.afterSendToClient(dataInfo);
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    public void checkAcceptedByClient() {
        List<DataInfo> dataInfos = infoSave.fetchData(FETCH_DATA_AFTER_SEND_TO_CLIENT);

        for (DataInfo dataInfo : dataInfos) {
            if (dataInfo.getSendToClientDate() != null && dataInfo.getAcceptClientDate() == null) {
                Account account = accountProc.getAccountById(dataInfo.getAccountId());
                Map<String, String> params = account.getParams();
                logger.debug(" params " + params.get(Service_Url));
                ServiceInterface service = serviceFactory.getService(params.get(Service_Url));
                logger.info(" account service " + service);
                params.put(account.getIdentityName(), dataInfo.getIdentity());
                List<ActParams> actParams = dbUtils.fetchData(ActParams.class, "ActParams", " actRequestId = " + dataInfo.getActRequestId());
                actParams.forEach(actParam -> {
                    params.put(actParam.getParamName(), actParam.getParamValue());
                });
                try {
                    if (!service.checkIdentityValues(params))
                        continue;
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    continue;
                }
                try {
                    infoSave.afterAcceptedByClient(dataInfo);
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

}
