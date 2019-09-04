package org.erachain.repositories;

import org.erachain.entities.account.Account;
import org.erachain.entities.datainfo.DataInfo;
import org.erachain.entities.request.ActParams;
import org.erachain.entities.request.ActRequest;
import org.erachain.entities.request.Request;
import org.erachain.service.ServiceFactory;
import org.erachain.service.ServiceInterface;
import org.erachain.utils.DateUtl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class DataClient {

    @Autowired
    private Logger logger;

    @Value("${Service_Url}")
    private String Service_Url;

    @Value("${FETCH_ACTREQ_ID_PARAM}")
    private String FETCH_ACTREQ_ID_PARAM;



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

    private ConcurrentMap<String, String> dataMap = new ConcurrentHashMap<>();

    private boolean checkData(String ident, String data) {
        if (dataMap.get(ident) == null) {
            dataMap.putIfAbsent(ident, data);
            logger.debug(" set ident " + ident + " data " + data);
            return false;
        }
        if(!dataMap.get(ident).equals(data)) {
            dataMap.replace(ident, data);
            logger.debug(" replace ident " + ident + " data " + data);
            return false;
        }
        return true;
    }

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

        Map<String, String> map = request.getParams(dbUtils, dateUtl);
        map.keySet().forEach(name -> {
            logger.debug(" name " + name + " value " + map.get(name));
        });
        params.putAll(map);

        List<String> idents = null;
        try {
            idents = service.getIdentityList(params);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }

        int debugIdents = 0;

        for (String ident : idents) {
            if (debugIdents++ > 10){
                logger.debug(" limits idents in debug {}", debugIdents);
                break;
            }
            params.put(account.getIdentityName(), ident);

            String json = null;
            try {
                json = service.getIdentityValues(params);
                if (checkData(ident, json))
                    continue;
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

    public void setClientData(int requestId, Map<String, byte[]> data) throws Exception {
        setClientData(accountProc.getRequestById(requestId), data);
    }

    private void setClientData(Request request, Map<String, byte[]> data) throws Exception {
//        try {
        logger.debug(" get act req id ");
        int actRequestId = 0;
        try {
            actRequestId = request.getActRequestId(this, dbUtils, dateUtl);
            if (actRequestId == 0) {
                actRequestId = request.setActRequestId(dbUtils, dateUtl);
                logger.info(" new act req id " + actRequestId);
                setData(request, actRequestId, data);
            } else {
                if (infoSave.checkDataWhere(actRequestId) > 0) {
                    logger.info(" found act req id " + actRequestId);
                    updateData(request, actRequestId, data);
                }
            }
        } catch (SQLException e) {
            logger.error(" error setting client for request " + request.getId() + " " + e.getMessage());
            throw e;
        }

        return;
    }

    private void updateData(Request request, int actRequestId, Map<String, byte[]> data) throws SQLException {
        List<DataInfo> dataInfos = infoSave.fetchDataWhere(actRequestId);
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

    private void setData(Request request, int actRequestId, Map<String, byte[]> data) throws SQLException {
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
    public int getActRequestId(int requestId, String paramName, String paramValue) throws Exception {
        logger.info(" paramName " + paramName + " paramValue " + paramValue);
        List<ActParams> actParams = dbUtils.fetchDataValues(ActParams.class, FETCH_ACTREQ_ID_PARAM, paramName, paramValue);
        for (ActParams actParam : actParams) {
            ActRequest actRequest = dbUtils.fetchData(ActRequest.class, actParam.getActRequestId());
            if (actRequest != null && actRequest.getRequestId() == requestId) {
                return actRequest.getId();
            }
        }
        return 0;
    }
}