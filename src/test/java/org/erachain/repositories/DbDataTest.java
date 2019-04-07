package org.erachain.repositories;

import org.erachain.entities.account.Account;
import org.erachain.entities.datainfo.DataEra;
import org.erachain.entities.datainfo.DataInfo;
import org.erachain.entities.request.ActRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

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
        List<DataInfo> list = infoSave.fetchData();
        logger.info(" size " + list.size());
        list.stream().forEach(dt -> {
            logger.info(dt.getIdentity() + " " + dt.getRunDate());
        });
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
    public void testInsertFromObj() {
        ActRequest actRequest = new ActRequest();
        actRequest.setPeriod("hour");
        actRequest.setRequestId(1);
        actRequest.setDateRun(new Timestamp(System.currentTimeMillis()));
    //    actRequest.setDateRun();
//        List<DataInfo> list = infoSave.fetchData();
//        list.forEach(dataInfo -> {
//        DataEra dataEra = new DataEra();
//        dataEra.setBlockTrId(dataInfo.getBlockId() + "-" + dataInfo.getTransId());
//        dataEra.setDataInfoId(dataInfo.getId());
//        dataEra.setSignature(dataInfo.getSignature());
//        dataEra.setPartNo(dataInfo.getPartNo());
        String sql = dbUtils.setObjToDb(actRequest,  "ActRequest", true);
        int id = dbUtils.exSqlStatement(sql);
        logger.info(" id " + id);
//        dataEra.setId(id);
//        });
    }
    @Test
    public void testFetchData() {
        List<DataEra> dataEras = dbUtils.fetchData(DataEra.class, "DataEra", "dataInfoId =1" );
        dataEras.forEach(dt -> {
            logger.info(dt.getSignature());
        });
    }
//    @Test
//    public void testUpdSt() {
//        List<DataEra> dataEras = dbUtils.fetchData(DataEra.class, "DataEra", "dataInfoId = 1" );
//        dbUtils.updObjById(dataEras.get(0), "DataEra");
//    }
//    @Test
//    public void testDataUpd() {
//        List<DataInfo> list = infoSave.fetchData();
//        dbUtils.updDbObjById(list.get(0), "DataInfo");
//    }

//    @Test
//    public void testAcrReqUpd() {
////        List<ActRequest> actRequests = dbUtils.fetchData(ActRequest.class, "ActRequest", null);
////        ActRequest actRequest = actRequests.get(1);
//        ActRequest actRequest = new ActRequest();
//        actRequest.setId(2);
//        actRequest.setRequestId(1);
//        actRequest.setPeriod("hour");
//        actRequest.setDateRun(new Timestamp(System.currentTimeMillis()));
//        dbUtils.updObjById(actRequest, "ActRequest");
//    }

    private void exSql(String sql) {
        dbUtils.exSqlStatement(sql);
    }

    @Test
    public void testTableDacr() {
        Arrays.stream(dbUtils.getColumnNameArray("DataInfo")).forEach(name -> {
            logger.info(" col " + name);
        });
    }

}
