package org.erachain.service.energetik;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EnergyServiceTest {

    @Autowired
    private EnergyService energyService;

    Map<String, String> params = new HashMap<>();

    private void setParams() {
        params.put("url", "https://app.yaenergetik.ru/api?v2");
        params.put("user", "s.klokov@erachain.org");
        params.put("password", "erachain");
        params.put("meter", "13857");
        params.put("type", "month");
        params.put("value", "2019-01");
    }

    @Test
    public void validateLogin() throws Exception {
        setParams();
        energyService.login(params);
    }
}
