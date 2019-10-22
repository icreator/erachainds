package org.erachain.service.mydom;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyDomServiceTest {

    @Autowired
    private MyDomService myDomService;

    Map<String, String> params = new HashMap<>();

    private void setParams() {

        params.put("accountUrl", "http://api-admin.testmoydom.ru");
        params.put("user", "");
        params.put("password", "");
        params.put("problem", "4391");
        params.put("authToken", "9u5bjskxv4pb7uotfk");
    }

    @Test
    public void login() throws Exception {
        setParams();
        myDomService.login(params);
    }

    @Test
    public void getIdentityList() throws Exception {
        setParams();
        myDomService.getIdentityList(params);
    }

    @Test
    public void getIdentityValues() throws Exception {
        setParams();
        myDomService.getIdentityValues(params);
    }

    @Test
    public void setIdentityValues() {
    }

    @Test
    public void checkIdentityValues() {
    }
}