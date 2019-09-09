package org.erachain.repositories;

import org.erachain.entities.account.Account;
import org.erachain.entities.datainfo.DataEra;
import org.erachain.entities.datainfo.DataInfo;
import org.erachain.entities.request.ActRequest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.valueextraction.Unwrapping;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DbDataTest {

    @Autowired
    private Logger logger;

    @Autowired
    private AccountProc accountProc;

    @Autowired
    private DbUtils dbUtils;

    @Autowired
    private InfoSave infoSave;

    @Value("${EraService_creator}")
    private String EraService_creator;

    @Value("${UPDATE_DATA_AFTER_SUBMIT}")
    private  String UPDATE_DATA_AFTER_SUBMIT;

    @Value("${UPDATE_DATA_AFTER_ACCEPT}")
    private String UPDATE_DATA_AFTER_ACCEPT;

    @Value("${UPDATE_DATA_AFTER_SEND_TO_CLIENT}")
    private String UPDATE_DATA_AFTER_SEND_TO_CLIENT;

    @Value("${UPDATE_DATA_ACCEPTED_BY_CLIENT}")
    private String UPDATE_DATA_ACCEPTED_BY_CLIENT;

//    @Value("${FETCH_DATA_AFTER_RUN}")
//    private String FETCH_DATA_AFTER_RUN;

//    @Value("${CHECK_DATA_AFTER_RUN}")
//    private String CHECK_DATA_AFTER_RUN;

    @Value("${FETCH_DATA_AFTER_SUBMIT}")
    private String FETCH_DATA_AFTER_SUBMIT;

    @Value("${CHECK_DATA_AFTER_SUBMIT}")
    private String CHECK_DATA_AFTER_SUBMIT;

    @Value("${CHECK_DATA_FOR_SUBMIT}")
    private String CHECK_DATA_FOR_SUBMIT;

    @Value("${FETCH_DATA_FOR_SUBMIT}")
    private String FETCH_DATA_FOR_SUBMIT;



    @Value("${FETCH_DATA_AFTER_ACCEPT}")
    private String FETCH_DATA_AFTER_ACCEPT;

    @Value("${CHECK_DATA_AFTER_ACCEPT}")
    private String CHECK_DATA_AFTER_ACCEPT;

    @Value("${FETCH_DATA_AFTER_SEND_TO_CLIENT}")
    private String FETCH_DATA_AFTER_SEND_TO_CLIENT;

    @Value("${CHECK_DATA_AFTER_SEND_TO_CLIENT}")
    private String CHECK_DATA_AFTER_SEND_TO_CLIENT;

    @Value("${GET_LAST_RECORD_BY_DATE}")
    private String GET_LAST_RECORD_BY_DATE;

    @Test
    public void getData1() {
        try {
//            String sql = CHECK_DATA_FOR_SUBMIT;
//            String sql = CHECK_DATA_FOR_SUBMIT.replace("?", Long.toString(new Date().getTime()));
//            logger.info(" sql " + sql);
            byte[] result = dbUtils.getData(GET_LAST_RECORD_BY_DATE, "4403", new Date(1567608297291L).getTime());
            logger.info(" result " + (result != null ? new String(result) : ""));

        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }
    @Test
    public void checkData() {
        try {
//            String sql = CHECK_DATA_FOR_SUBMIT;
            String sql = CHECK_DATA_FOR_SUBMIT.replace("?", Long.toString(new Date().getTime()));
            logger.info(" sql " + sql);
            int result = dbUtils.checkData(sql);
            logger.info(" result " + result);

        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    @Test
    public void createData() {
        DataInfo dataInfo = new DataInfo();
        dataInfo.setData("111111111111111111111111111111111111".getBytes());
        dataInfo.setAccountId(1);
        dataInfo.setIdentity("2222222");
        try {
            infoSave.saveData(dataInfo);
            logger.info(" dataInfo saved  " + dataInfo.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    @Test
    public void accountTest() {
        List<Account> list = accountProc.getAccounts();
        logger.info(" size " + list.size());
        list.stream().forEach(account -> {
            logger.info(account.getAccountUrl() + " "  + " " +  account.getIdentityName());
        });
    }
    @Test
    public void dataFetch() {
        List<DataInfo> list = infoSave.fetchData(FETCH_DATA_FOR_SUBMIT.replace("?", Long.toString(new Date().getTime())));
        logger.info(" size " + list.size());
        list.stream().forEach(dt -> {
            logger.info(dt.getIdentity() + " " + dt.getRunDate());
        });
    }
    @Test
    public void testfetchDataForClient() {
        String ident = "17872";
        Map<String, String> params = new HashMap<>();
        params.put("type", "month");
        params.put("value", "2019-02");
        try {
            String result = infoSave.fetchDataForClient(ident, params);
            logger.info(" result " + result);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testInsertFromObj() {
        ActRequest actRequest = new ActRequest();
        actRequest.setPeriod("hour");
        actRequest.setRequestId(1);
        actRequest.setDateRun(new Timestamp(System.currentTimeMillis()));

        String sql = dbUtils.setObjToDb(actRequest,  "ActRequest", true);
        int id = dbUtils.exSqlStatement(sql);
        logger.info(" id " + id);
//        dataEra.setId(id);
//        });
    }
    @Test
    public void testFetchData() {
        int actRequestId = 1;
        List<DataInfo> dataInfos = infoSave.fetchDataWhere(actRequestId);
        dataInfos.forEach(dataInfo -> {
            logger.info(" dataInfo " + dataInfo.getIdentity());
        });
//        List<DataEra> dataEras = dbUtils.fetchData(DataEra.class, "DataEra", "dataInfoId =1" );
//        dataEras.forEach(dt -> {
//            logger.info(dt.getSignature());
//        });
    }

    private void exSql(String sql) {
        dbUtils.exSqlStatement(sql);
    }

    @Test
    public void testTableDacr() {
        Arrays.stream(dbUtils.getColumnNameArray("DataInfo")).forEach(name -> {
            logger.info(" col " + name);
        });
    }
    @Test
    public void testSql1() {
        String sql = "select  runDate, data from DataInfo";
        List<DataInfo> dataInfos = dbUtils.fetchData(DataInfo.class, sql);
        dataInfos.forEach(dataInfo -> {
            logger.info(dataInfo.getRunDate() + " " + dataInfo.getData());
        });
    }

}
