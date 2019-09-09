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
import java.util.ArrayList;
import java.util.List;

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

    public List<String> getPeers() {
        JSONObject obj = getJson("peers.json");
        JSONArray peers = obj.getJSONArray("peers");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < peers.length(); i++) {
            //    logger.info("peer " + peers.get(i).toString());
            list.add(peers.get(i).toString());
        }
        return list;

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
}
