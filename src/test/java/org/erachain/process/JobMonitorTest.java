package org.erachain.process;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JobMonitorTest {

    @Autowired
    private Logger logger;

    @Autowired
    private JobMonitor jobMonitor;

    @Test
    public void checkReady() {
//        jobMonitor.checkData();
        jobMonitor.checkReadyAccounts();
    }

}
