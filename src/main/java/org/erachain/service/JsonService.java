package org.erachain.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Service
public class JsonService {

    @Autowired
    private Logger logger;

    public <T> T getValue(String jsonString, String key) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        return (T) getValue(jsonObject, key);
    }
    public <T> T getValue(JSONObject jsonObject, String key) {
        return (T) jsonObject.get(key);
    }

    public Map<String, Object> getObjToMap(JSONObject jsonObject) {
        Map<String, Object> map = new HashMap<>();
        jsonObject.keys().forEachRemaining(key -> {
            map.put(key, jsonObject.get(key));
        });
        return map;
    }
    public JSONObject getJson(String file) {
        File resource = null;
        String payStr = null;
        try {
            resource = new ClassPathResource(
                    file).getFile();
            payStr = new String(
                    Files.readAllBytes(resource.toPath()));
            //    logger.info(payStr);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return new JSONObject(payStr);
    }
    public JSONObject setMapToObj(Map<String, Object> map, JSONObject jsonObject) {
        jsonObject.keys().forEachRemaining(key -> {
            if (map.get(key) != null)
                jsonObject.put(key, map.get(key));
        });
        return jsonObject;
    }
    public String getDataMapList(List<Map<String, Object>> list) throws Exception {
        String json1 = "{\"result\":[]}";
        String dt1 = "{\"date\":\"\",\"data\":\"\"}";
        JSONObject jsonObject = new JSONObject(json1);
        JSONArray jsonArray = jsonObject.getJSONArray("result");
        try {
            list.forEach(map ->  {
                JSONObject dt = new JSONObject(dt1);
                dt.put("date", map.get("date"));
                String data = new String((byte[]) map.get("data"));
                dt.put("data", new JSONObject(data));
                jsonArray.put(dt);
            });
            jsonObject.put("result", jsonArray);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return jsonObject.toString();
    }
}
