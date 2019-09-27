package org.erachain.service;


import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Service
@Scope(value = "prototype")
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

    public String getJsonResult(String url) throws Exception {
        HttpEntity<String> request = new HttpEntity<>(headers);

        //String response = new RestTemplate().postForObject(url, request, String.class);
        //String response = new RestTemplate().getForObject(url, request, String.class);
        //RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = new RestTemplate().exchange(url, HttpMethod.GET, request, String.class);
        return response.getBody();
    }
    public String getJsonResult(String url, String jsonRequest) throws Exception {
        HttpEntity<String> request = new HttpEntity<>(jsonRequest, headers);

        String response = new RestTemplate().postForObject(url, request, String.class);

        return response;
    }

    public String getResult(String url, String anyString) throws Exception  {

//        HttpEntity<String> request = new HttpEntity<>(anyString, headers);
        HttpEntity<String> request = new HttpEntity<>(anyString);

        RestTemplate restTemplate = new RestTemplate();
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        stringHttpMessageConverter.setWriteAcceptCharset(true);
        for (int i = 0; i < restTemplate.getMessageConverters().size(); i++) {
            if (restTemplate.getMessageConverters().get(i) instanceof StringHttpMessageConverter) {
                restTemplate.getMessageConverters().remove(i);
                restTemplate.getMessageConverters().add(i, stringHttpMessageConverter);
                break;
            }
        }

        String response = restTemplate.postForObject(url, request, String.class);

        return response;
    }
    public String getResult(String url) throws Exception {
        String response = new RestTemplate().getForObject(url, String.class);

        return response;
    }
    public  String addParams(String url, String[] urlParams, Map<String, String> params) {
        final StringBuffer result = new StringBuffer(url);
        if (urlParams != null) {
            for(String p : urlParams) {
                result.append("/" + p);
            }
        }
        if (params == null || params.isEmpty())
            return result.toString();
        result.append("?");
        boolean started = false;
        params.keySet().forEach(name -> {

            result.append(name + "=" + params.get(name) + "&");

        });
        result.deleteCharAt(result.lastIndexOf("&"));
        return result.toString();
    }
}
