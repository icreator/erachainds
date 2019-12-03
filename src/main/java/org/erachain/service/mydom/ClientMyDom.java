package org.erachain.service.mydom;

import org.erachain.service.JsonService;
import org.erachain.service.RestClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ClientMyDom {
    private final Logger logger;

    @Autowired
    private RestClient restClient;

    @Autowired
    private JsonServiceMyDom jsonServiceMyDom;

    @Autowired
    private JsonService jsonService;

    private String url;

    final private String problemPath = "/problem-journal/v1/block-chain/changes-history-status";
    final private String loginPath = "/auth/v1/login";

    private String accessToken ="";

    public ClientMyDom(Logger logger) {
        this.logger = logger;
    }


    public void clientLogin(Map<String, String> params) throws Exception {

        if (!accessToken.isEmpty())
            return;

        if (!params.containsKey("authToken")) {
            throw new IllegalArgumentException(" not found required Request parameter \"authToken\"");
        }
        clientLogin(params.get("accountUrl"),
                params.get("user"), params.get("password"), params.get("authToken"));
        params.put("AccessToken", accessToken);
    }
    private void clientLogin(String url, String user, String pass, String authToken) throws Exception {
        logger.info(" login " + url + " " + user);
        String json = restClient.getJsonResult(url+loginPath, jsonServiceMyDom.getAuth(user, pass, authToken).toString());
        String error = jsonServiceMyDom.checkForError(json);
        if (error != null) {
            logger.error(" login error " + error);
            throw new Exception(" login error " + error);
        }
        JSONObject response = jsonService.getValue(json, "response");
        accessToken = jsonService.getValue(response, "accessToken");

        restClient.addHeader("AccessToken", accessToken);
        this.url = url;
        logger.info("end login " + accessToken);
    }

    /*
    public  List<String> getNetWorkList(Map<String, String> params) throws Exception {
        logger.info(" NetWorkList ");
        if (params.get("AccessToken") == null) {
            this.clientLogin(params);
        }
        String response = restClient.getJsonResult(url,
                jsonServiceMyDom.getNetWorkJson().toString());
        return jsonServiceMyDom.getNetWorkList(response);
    }
    */
    public  List<String> getProblemList(Map<String, String> params) throws Exception {
        logger.info(" ProblemList ");

        clientLogin(params);

        String response = restClient.getJsonResult(url+problemPath);
        return jsonServiceMyDom.getProblemList(response);
    }
    public String getProblemResult(Map<String, String> params) throws Exception {
            clientLogin(params);

//        String type = params.get("type") == null ? "month" : params.get("type");
//        params.put("type", type);
//        String value = params.get("value");
//        //String format = params.get("format");
//        if (value == null) {
//            SimpleDateFormat format = new SimpleDateFormat(params.get("format") == null ? "yyyy-MM-dd" : params.get("format"));
//
//            Date date = new Date();
//            value = format.format( date);
//            params.put("value", value);
//            value = type.equalsIgnoreCase("month") ? dateString.substring(0, 7) : dateString;
//        }
        return getProblemResult(params, params.get("problem"));
    }
    private String getProblemResult(Map<String, String> params, String identity) throws Exception {

       String json = restClient.getJsonResult(url+problemPath+"//"+identity);
        String error = jsonServiceMyDom.checkForError(json);
        if (error != null) {
            logger.error(" get problem " + error);
            throw new Exception(" get problem " + error);
        }
        logger.info(" ProblemResult for " + identity + " size: " + String.valueOf(json.length()));
        logger.debug(json);
        return json;
    }
    /*
    public String setMeterResult(Map<String, String> params) throws Exception {
        if (params.get("AccessToken") == null) {
            this.clientLogin(params);
        }
        String type = params.get("type");
        String value = params.get("value");
        return setMeterResult(params, params.get("meter"), type, value, params.get("transaction"));
    }
    private String setMeterResult(Map<String, String> params, String meter, String type, String value, String transaction) throws Exception {
        logger.info(" get MeterResult for " + meter + " " + type + " " + value + " transaction " + transaction);
        String result = restClient.getJsonResult(url,
                jsonServiceMyDom.setMeterResultJson(params, meter, type, value, transaction).toString());
        logger.info("  MeterResult  " + result);
        return result;
    }
    public String checkMeterResult(Map<String, String> params) throws Exception {
        if (params.get("AccessToken") == null) {
            this.clientLogin(params);
        }
        String type = params.get("type");
        String value = params.get("value");
        String result = checkMeterResult(params, params.get("meter"), type, value);
        return result;
    }
    private String checkMeterResult(Map<String, String> params, String meter, String type, String value) throws Exception {
        logger.info(" check MeterResult for " + meter + " " + type + " " + value );
        String result = restClient.getJsonResult(url,
                jsonServiceMyDom.checkMeterResultJson(params, meter, type, value).toString());
        logger.debug("  MeterResult  " + result);
        return result;
    }
    */
}
