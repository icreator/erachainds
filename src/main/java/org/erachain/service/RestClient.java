package org.erachain.service;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class RestClient {
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
    public RestClient() {
        Map map = new HashMap<String, String>();
        map.put("Content-Type", "application/json");
//        map.put("X-Session-Id", sessionId);
        headers.setAll(map);

    }
    public void addHeader(String name, String value) {
        headers.add(name, value);
    }
    public String getJsonResult(String url, String jsonRequest) {
        HttpEntity<String> request = new HttpEntity<>(jsonRequest, headers);

        String response = new RestTemplate().postForObject(url, request, String.class);

        return response;
    }
}
