package org.erachain.repositories;

import org.erachain.entities.account.Account;
import org.erachain.entities.request.Request;
import org.erachain.utils.DateUtl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataClientTest {

    @Autowired
    private Logger logger;

    @Autowired
    private DbUtils dbUtils;

    @Autowired
    private InfoSave infoSave;

    @Autowired
    private AccountProc accountProc;

    @Autowired
    private DataClient dataClient;

    @Autowired
    private DateUtl dateUtl;

    @Test
    public void DataClient2() {
        Account account= accountProc.getAccounts().get(0);
        Request request = accountProc.getRequests(account.getId()).get(0);
        try {
            int id = request.getActRequestId(dbUtils, dateUtl);
            logger.info(" getActRequestId " + id);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Test
    public void DataClient1() {
        Account account= accountProc.getAccounts().get(0);
        Request request = accountProc.getRequests(account.getId()).get(0);

        try {
            Map<String, byte[]> data = dataClient.getDataFromClient(account, request);
            data.keySet().forEach(ident -> {
                logger.info(" ident " + ident);
                logger.info( " data " + data.get(ident).toString());
            });
            dataClient.setClientData(request, data);
            logger.info(" isCurrent " + request.isCurrentDate() + " run date " + request.getLastRun());
        } catch (Exception e) {
            logger.error(" test " + e.getMessage());
        }
    }



 }
