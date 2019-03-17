package org.erachain.service.energetik;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class JsonService {
    public  JSONObject getAuth(String user, String password) {
        String verifyCode = null;
        JSONObject requestBody = new JSONObject();
        requestBody.put("jsonrpc", "2.0");
        requestBody.put("method", "auth.login");
        JSONObject params = new JSONObject();
        params.put("mode", "user");
//        params.put("user", "s.klokov@erachain.org");
        params.put("user", user);
//        params.put("password", "erachain");
        params.put("password", password);
        params.put("verifyCode", verifyCode);
        requestBody.put("id", 1);
        requestBody.put("params", params);
//        System.out.println(requestBody.toString());
        return requestBody;
    }
    public  JSONObject getNetWorkJson() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("jsonrpc", "2.0");
        requestBody.put("method", "network.list");
        requestBody.put("id", 1);
        return requestBody;
    }
    public  List<String> getNetWorkList(String response) {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray result = jsonObject.getJSONArray("result");
        List<String> list =  new ArrayList<>();
        result.toList().forEach(o -> {
            String id = ((Map) o).get("id").toString();
//            System.out.println(id);
            list.add(id);
        });
        return list;
    }
    public  JSONObject getMeterJson(String netWork) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("jsonrpc", "2.0");
        requestBody.put("method", "meter.list");
        requestBody.put("id", 1);
        JSONObject params = new JSONObject();
        params.put("network", Integer.parseInt(netWork));
        requestBody.put("params", params);
        return requestBody;
    }
    public  JSONObject getMeterResultJson(String meter, String type, String value) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("jsonrpc", "2.0");
        requestBody.put("method", "reading.list");
        requestBody.put("id", 1);
        JSONObject params = new JSONObject();
        params.put("meter", Integer.parseInt(meter));
        params.put("mode", "archive");
        params.put("sort", "asc");
        JSONObject period = new JSONObject();
        period.put("type", type);
        period.put("value", value);
        params.put("period", period);
        requestBody.put("params", params);
        String[] array = {"zones", "errors"};
        JSONArray include = new JSONArray(Arrays.asList(array));
        params.put("include", include);
        System.out.println("result " + requestBody.toString());
        return requestBody;
    }
    public  List<String> getMeterList(String response) {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray result = jsonObject.getJSONArray("result");
        List<String> list =  new ArrayList<>();
        result.toList().forEach(o -> {
            String id = ((Map) o).get("id").toString();
            list.add(id);
        });
        return list;
    }
    public String getValue(String jsonString, String key) {
        JSONObject jsonObject = new JSONObject(jsonString);
        return jsonObject.get(key).toString();
    }
    private String getValue(JSONObject jsonObject, String key) {
        return jsonObject.get(key).toString();
    }
}
