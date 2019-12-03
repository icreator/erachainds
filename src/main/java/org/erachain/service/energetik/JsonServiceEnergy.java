package org.erachain.service.energetik;

import org.erachain.service.JsonService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class JsonServiceEnergy {

    private final Logger logger;

    @Autowired
    private JsonService jsonService;

    public JsonServiceEnergy(Logger logger) {
        this.logger = logger;
    }


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
    public  JSONObject getMeterResultJson(Map<String, String> pars, String meter, String type, String value) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("jsonrpc", "2.0");
        requestBody.put("method", "reading.list");
        requestBody.put("id", 1);
        JSONObject params = new JSONObject();
        params.put("meter", Integer.parseInt(meter));
        params.put("mode", pars.get("mode") == null ? "archive" : pars.get("mode"));
        params.put("sort", "asc");
        JSONObject period = new JSONObject();
        period.put("type", type);
        period.put("value", value);
        params.put("period", period);
        requestBody.put("params", params);
        String[] array = {"zones", "errors"};
        JSONArray include = new JSONArray(Arrays.asList(array));
        params.put("include", include);
     //   System.out.println("result " + requestBody.toString());
        return requestBody;
    }
    public  JSONObject setMeterResultJson(Map<String, String> pars, String meter, String type, String value, String transaction) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("jsonrpc", "2.0");
        requestBody.put("method", "reading.setListBlockchain");
        requestBody.put("id", 1);
        JSONObject params = new JSONObject();
        params.put("meter", Integer.parseInt(meter));
        params.put("mode", pars.get("mode") == null ? "archive" : pars.get("mode"));
        params.put("sort", "asc");
        params.put("transaction", transaction);
        JSONObject period = new JSONObject();
        period.put("type", type);
        period.put("value", value);
        params.put("period", period);
        requestBody.put("params", params);
        String[] array = {"zones", "errors"};
        JSONArray include = new JSONArray(Arrays.asList(array));
        params.put("include", include);
        logger.debug("result " + requestBody.toString());
    //    System.out.println("result " + requestBody.toString());
        return requestBody;
    }
    public Boolean checkMeterResult(String jsonString) {
        JSONObject result = jsonService.getValue(jsonString, "result");
        Boolean value = jsonService.getValue(result, "value");
        logger.info(" checkMeterResult " + value);
        return value;
    }
    public  JSONObject checkMeterResultJson(Map<String, String> pars, String meter, String type, String value) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("jsonrpc", "2.0");
        requestBody.put("method", "reading.getListBlockchain");
        requestBody.put("id", 1);
        JSONObject params = new JSONObject();
        params.put("meter", Integer.parseInt(meter));
        params.put("mode", pars.get("mode") == null ? "archive" : pars.get("mode"));
        params.put("sort", "asc");
//        params.put("transaction", transaction);
        JSONObject period = new JSONObject();
        period.put("type", type);
        period.put("value", value);
        params.put("period", period);
        requestBody.put("params", params);
        String[] array = {"zones", "errors"};
        JSONArray include = new JSONArray(Arrays.asList(array));
        params.put("include", include);
        logger.info(" result size: " + requestBody.toString().length());
        logger.debug("result " + requestBody.toString());
        //    System.out.println("result " + requestBody.toString());
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
//    public <T> T getValue(String jsonString, String key) throws JSONException {
//        JSONObject jsonObject = new JSONObject(jsonString);
//        return (T) getValue(jsonObject, key);
//    }
//    private <T> T getValue(JSONObject jsonObject, String key) {
//        return (T) jsonObject.get(key);
//    }

    public String checkForError(String json) {
        JSONObject jsonObject = new JSONObject(json);
        StringBuffer error = new StringBuffer("");
        jsonObject.keys().forEachRemaining(key -> {
            if("error".equalsIgnoreCase(key)) {
                error.append(jsonObject.get(key).toString());
            }
        });
        String message = error.toString();
        if(!message.isEmpty()) {
            return message;
        }
        return null;
    }
}
