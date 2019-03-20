package org.erachain.repositories;

import org.erachain.entities.account.Account;
import org.erachain.entities.datainfo.DataInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DbDataTest {

    @Autowired
    private Logger logger;

    @Autowired
    private AccountProc accountProc;

    @Autowired
    private InfoSave infoSave;

    @Test
    public void accountTest() {
        List<Account> list = accountProc.getAccounts();
        logger.info(" size " + list.size());
        Account account = list.get(0);
        logger.info(account.getAccountUrl() + " " + account.getRunDate() );
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
        dataInfo.setIdentity("111111111");
        try {
            infoSave.saveData(dataInfo);
            logger.info(" dataInfo saved  " + dataInfo.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
