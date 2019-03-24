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

    public String getResult(String url, String anyString) {
        HttpEntity<String> request = new HttpEntity<>(anyString);

        String response = new RestTemplate().postForObject(url, request, String.class);

        return response;
    }
    public  String addParams(String url, String[] urlParams, Map<String, String> params) {
        final StringBuffer result = new StringBuffer(url);
        if (urlParams != null) {
            for(String p : urlParams) {
                result.append("/" + p);
            }
        }
        if (params.isEmpty())
            return result.toString();
        result.append("?");
        boolean started = false;
        params.keySet().stream().forEach(name -> {

            result.append(name + "=" + params.get(name) + "&");

        });
        result.deleteCharAt(result.lastIndexOf("&"));
        return result.toString();
    }
}
