package org.erachain.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JsonServiceTest {

    @Autowired
    private Logger logger;

    @Autowired
    private JsonService jsonService;

    String json1 = "{\"result\":[]}";

    String dt1 = "{\"date\":\"\",\"data\":\"\"}";

    String data1 = "{\"status\":\"ok\",\"response\":{\"changes\":[]},\"access\":{\"secretToken\":\"wa49q2wth7899g2i87\"}}";

    @Test
    public void tset1() {
        JSONObject jsonObject = new JSONObject(json1);
        JSONArray jsonArray = jsonObject.getJSONArray("result");
        JSONObject data = new JSONObject(data1);
        JSONObject dt = new JSONObject(dt1);
        dt.put("date", new Date().getTime());
        dt.put("data", data);
        jsonArray.put(dt);
        jsonArray.put(dt);
        jsonObject.put("result", jsonArray);
        logger.info(jsonObject.toString());
    }
}