package org.erachain.service.energetik;

import org.erachain.service.JsonService;
import org.erachain.service.RestClient;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ClientEnergy {
    private final Logger logger;

    @Autowired
    private RestClient restClient;

    @Autowired
    private JsonServiceEnergy jsonServiceEnergy;

    @Autowired
    private JsonService jsonService;

    private String Energy_Url;

    private String sessionId;

    public ClientEnergy(Logger logger) {
        this.logger = logger;
    }

    public void clientLogin(Map<String, String> params) throws Exception {
        clientLogin(params.get("accountUrl"),
                params.get("user"), params.get("password"));
        params.put("sessionId", sessionId);
    }
    private void clientLogin(String url, String user, String pass) throws Exception {
        logger.info(" login " + url + " " + user);
        String json = restClient.getJsonResult(url, jsonServiceEnergy.getAuth(user, pass).toString());
        String error = jsonServiceEnergy.checkForError(json);
        if (error != null) {
            logger.error(" login error " + error);
            throw new Exception(" login error " + error);
        }
        sessionId = jsonService.getValue(json, "result");
        restClient.addHeader("X-Session-Id", sessionId);
        Energy_Url = url;
        logger.info("end login " + sessionId);
    }

    public  List<String> getNetWorkList(Map<String, String> params) throws Exception {
        logger.info(" NetWorkList ");
        if (params.get("sessionId") == null) {
            this.clientLogin(params);
        }
        String response = restClient.getJsonResult(Energy_Url,
                jsonServiceEnergy.getNetWorkJson().toString());
        return jsonServiceEnergy.getNetWorkList(response);
    }
    public  List<String> getMeterList(String netWork) throws Exception {
        logger.info(" MeterList for " + netWork);
        String response = restClient.getJsonResult(Energy_Url,
                jsonServiceEnergy.getMeterJson(netWork).toString());
        return jsonServiceEnergy.getMeterList(response);
    }
    public String getMeterResult(Map<String, String> params) throws Exception {
        if (params.get("sessionId") == null) {
            this.clientLogin(params);
        }

        String type = params.get("type") == null ? "month" : params.get("type");
        params.put("type", type);
        String value = params.get("value");
        //String format = params.get("format");
        if (value == null) {
            SimpleDateFormat format = new SimpleDateFormat(params.get("format") == null ? "yyyy-MM-dd" : params.get("format"));

            Date date = new Date();
            value = format.format( date);
            params.put("value", value);
//            value = type.equalsIgnoreCase("month") ? dateString.substring(0, 7) : dateString;
        }
        return getMeterResult(params, params.get("meter"), type, value);
    }
    private String getMeterResult(Map<String, String> params, String meter, String type, String value) throws Exception {

       String json = restClient.getJsonResult(Energy_Url,
               jsonServiceEnergy.getMeterResultJson(params, meter, type, value).toString());
        String error = jsonServiceEnergy.checkForError(json);
        if (error != null) {
            logger.error(" get meter " + error);
            throw new Exception(" get meter " + error);
        }
        logger.info(" MeterResult for " + meter + " " + type + " " + value + " size: " + String.valueOf(json.length()));
        logger.debug(json);
        return json;
    }
    public String setMeterResult(Map<String, String> params) throws Exception {
        if (params.get("sessionId") == null) {
            this.clientLogin(params);
        }
        String type = params.get("type");
        String value = params.get("value");
        return setMeterResult(params, params.get("meter"), type, value, params.get("transaction"));
    }
    private String setMeterResult(Map<String, String> params, String meter, String type, String value, String transaction) throws Exception {
        logger.info(" get MeterResult for " + meter + " " + type + " " + value + " transaction " + transaction);
        String result = restClient.getJsonResult(Energy_Url,
                jsonServiceEnergy.setMeterResultJson(params, meter, type, value, transaction).toString());
        logger.info("  MeterResult  " + result);
        return result;
    }
    public String checkMeterResult(Map<String, String> params) throws Exception {
        if (params.get("sessionId") == null) {
            this.clientLogin(params);
        }
        String type = params.get("type");
        String value = params.get("value");
        String result = checkMeterResult(params, params.get("meter"), type, value);
        return result;
    }
    private String checkMeterResult(Map<String, String> params, String meter, String type, String value) throws Exception {
        logger.info(" check MeterResult for " + meter + " " + type + " " + value );
        String result = restClient.getJsonResult(Energy_Url,
                jsonServiceEnergy.checkMeterResultJson(params, meter, type, value).toString());
        logger.debug("  MeterResult  " + result);
        return result;
    }
}
