package org.erachain.service.energetik;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EnergyServiceTest {

    @Autowired
    private Logger logger;

    @Autowired
    private EnergyService energyService;

    Map<String, String> params = new HashMap<>();

    private void setParams() {
        params.put("accountUrl", "https://app.yaenergetik.ru/api?v2");
        params.put("user", "s.klokov@erachain.org");
        params.put("password", "erachain");
        params.put("meter", "13857");
        params.put("type", "month");
        params.put("mode", "all");
        params.put("value", "2019-01");
//        params.put("type", "day");
//        params.put("value", "2019-01-05");
    }

    @Test
    public void validateLogin() throws Exception {
        setParams();
        energyService.login(params);
    }
    @Test
    public void validateIdentityList() throws Exception {
        setParams();
        List<String> list = energyService.getIdentityList(params);
        logger.info(" list size " + list.size());
    }
    @Test
    public void validateIdentityValue()  throws Exception {
        setParams();
        String value = energyService.getIdentityValues(params);
        logger.info(" identity value " + value);

    }
}
