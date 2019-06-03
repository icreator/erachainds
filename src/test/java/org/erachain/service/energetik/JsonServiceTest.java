package org.erachain.service.energetik;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JsonServiceTest {

    @Autowired
    private Logger logger;

    @Autowired
    private JsonService jsonService;

    Map<String, String> params = new HashMap<>();

    private void setParams() {
        params.put("accountUrl", "https://app.yaenergetik.ru/api?v2");
        params.put("user", "s.klokov@erachain.org");
        params.put("password", "erachain");
        params.put("meter", "13855");
        params.put("type", "month");
        params.put("value", "2019-02");
        params.put("transaction", "352232-01");
//        params.put("type", "day");
//        params.put("value", "2019-01-05");
    }
    @Test
    public void testSetMeterResultJson() {
        setParams();
        String type = params.get("type");
        String value = params.get("value");
        String result = jsonService.setMeterResultJson(params, params.get("meter"), type, value, params.get("transaction")).toString();
        logger.info(result);
    }
    @Test
    public void testCheckMeterResultJson() {
        setParams();
        String type = params.get("type");
        String value = params.get("value");
        String result = jsonService.checkMeterResultJson(params, params.get("meter"), type, value).toString();
        logger.info(result);
    }
}
