package org.erachain.service.eraService;

import org.erachain.entities.account.Account;
import org.erachain.repositories.AccountProc;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EraClientTest {

    @Autowired
    private Logger logger;

    @Autowired
    private EraClient eraClient;

    @Autowired
    private AccountProc accountProc;

    @Test
    public void testClient() {
        Account account = accountProc.getAccountById(1);
        try {
            String sign = eraClient.getSignForData(account, "test");
            logger.info(" sign " + sign);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
