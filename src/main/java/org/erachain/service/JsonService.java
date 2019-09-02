package org.erachain.service;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
