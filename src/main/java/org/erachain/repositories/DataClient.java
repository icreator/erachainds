package org.erachain.repositories;

import org.erachain.entities.account.Account;
import org.erachain.entities.datainfo.DataInfo;
import org.erachain.entities.request.Request;
import org.erachain.service.ServiceFactory;
import org.erachain.service.ServiceInterface;
import org.erachain.utils.DateUtl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Repository
public class DataClient {

    @Autowired
    private Logger logger;

    @Value("${Service_Url}")
    private String Service_Url;


    @Autowired
    private ServiceFactory serviceFactory;

    @Autowired
    private DbUtils dbUtils;

    @Autowired
    private DateUtl dateUtl;

    @Autowired
    private InfoSave infoSave;

    @Autowired
    private AccountProc accountProc;

    public Map<String, byte[]> getDataFromClient(int accountId, int requestId) throws Exception {
        Account account = accountProc.getAccountById(accountId);
        Request request = accountProc.getRequestById(requestId);
        return getDataFromClient(account, request);
    }
    public Map<String, byte[]> getDataFromClient(Account account, Request request) throws Exception {
        Map<String, byte[]> identData = new HashMap<>();
        request.setLastReceived();
        Map<String, String> params = account.getParams();
        logger.info(" params " + params.get(Service_Url));
        ServiceInterface service = serviceFactory.getService(params.get(Service_Url));
        logger.info(" account service " + service);
        List<String> idents = null;
        try {
            idents = service.getIdentityList(params);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
        List<DataInfo> dataInfos = new ArrayList<>();
        for (String ident : idents) {
            params.put(account.getIdentityName(), ident);
            Map<String, String> map = request.getParams(dbUtils, dateUtl);
            map.keySet().forEach(name -> {
                logger.info(" name " + name + " value " + map.get(name));
            });
            params.putAll(map);
            String json = null;
            try {
                json = service.getIdentityValues(params);
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw e;
            }
            if (json != null && !json.isEmpty()) {
                byte[] data = json.getBytes();
                identData.put(ident, data);

            }

        }
        return identData;
    }

    public void setClientData(Request request, Map<String, byte[]> data) throws Exception {
//        try {
        logger.info(" get act req id ");
        int actRequestId = 0;
        try {
            actRequestId = request.getActRequestId(dbUtils, dateUtl);
        } catch (SQLException e) {
            logger.error(" error getting act req id " + e.getMessage());
            throw e;
        }
        logger.info(" act req id " + actRequestId);
        List<DataInfo> dataInfos = infoSave.fetchDataWhere(" actRequestId = " + actRequestId);
        if (dataInfos != null && !dataInfos.isEmpty()) {
            for (DataInfo dataInfo : dataInfos) {
                String identity = dataInfo.getIdentity();
                dataInfo.setData(data.get(identity));
                dataInfo.setRunDate(new Timestamp(new Date().getTime()));
                try {
                    logger.info(" save after run ");
                    infoSave.afterRun(dataInfo);
                } catch (SQLException e) {
                    logger.error(" save after run ");
                    throw e;
                }
            }
            accountProc.afterRun(request);
            return;
        }
        for (String ident : data.keySet()) {
            DataInfo dataInfo = new DataInfo();
            dataInfo.setRunDate(new Timestamp(System.currentTimeMillis()));
            dataInfo.setAccountId(request.getAccountId());
            dataInfo.setIdentity(ident);
            dataInfo.setData(data.get(ident));
            dataInfo.setActRequestId(actRequestId);
            logger.info("data saved  for ident " + dataInfo.getIdentity() + " " + dataInfo.getRunDate());
            try {
                infoSave.saveData(dataInfo);
            } catch (SQLException e) {
                logger.error(" error after saveData ");
                throw e;
            }
        }
        accountProc.afterRun(request);
        return;
    }
}