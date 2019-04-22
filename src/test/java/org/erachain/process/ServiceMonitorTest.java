package org.erachain.process;

import org.erachain.utils.loggers.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceMonitorTest {

    @Autowired
    private Logger logger;

    @Autowired
    private ServiceMonitor serviceMonitor;

//    @Test
//    public void testAccounts() {
//        serviceMonitor.checkAccounts();
//    }

    @Test
    public void testDataSubmit() {
        try {
            serviceMonitor.checkDataSubmit();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Test
    public void testDataAccept() {
        try {
            serviceMonitor.checkDataAccept();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Test
    public void testAfterDataAccept() {
        serviceMonitor.checkSendToClient();
    }
    @Test
    public void testCheckAcceptedByClient() {
        serviceMonitor.checkAcceptedByClient();
    }

}
