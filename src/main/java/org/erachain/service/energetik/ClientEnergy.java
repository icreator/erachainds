package org.erachain.service.energetik;

import org.apache.commons.lang3.time.DateUtils;
import org.erachain.service.RestClient;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ClientEnergy {
    @Autowired
    private Logger logger;

    @Autowired
    private RestClient restClient;

    @Autowired
    private JsonService jsonService;

    private String Energy_Url;

    private String sessionId;

    public void clientLogin(Map<String, String> params)  {
        clientLogin(params.get("accountUrl"),
                params.get("user"), params.get("password"));
        params.put("sessionId", sessionId);
    }
    private void clientLogin(String url, String user, String pass) {
        logger.info(" login " + url + " " + user);
        String json = restClient.getJsonResult(url, jsonService.getAuth(user, pass).toString());
        sessionId = jsonService.getValue(json, "result");
        restClient.addHeader("X-Session-Id", sessionId);
        Energy_Url = url;
        logger.info("end login " + sessionId);
    }

    public  List<String> getNetWorkList(Map<String, String> params) {
        logger.info(" NetWorkList ");
        if (params.get("sessionId") == null) {
            this.clientLogin(params);
        }
        String response = restClient.getJsonResult(Energy_Url,
                jsonService.getNetWorkJson().toString());
        return jsonService.getNetWorkList(response);
    }
    public  List<String> getMeterList(String netWork) {
        logger.info(" MeterList for " + netWork);
        String response = restClient.getJsonResult(Energy_Url,
                jsonService.getMeterJson(netWork).toString());
        return jsonService.getMeterList(response);
    }
    public String getMeterResult(Map<String, String> params) {
        if (params.get("sessionId") == null) {
            this.clientLogin(params);
        }

        String type = params.get("type") == null ? "month" : params.get("type");
        String value = params.get("value");
        if (value == null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            Date date = new Date();
            switch (type) {
                case ("month") :
                    date = DateUtils.addMonths(new Date(), -1);
                    break;
                case ("week") :
                    date = DateUtils.addWeeks(new Date(), -1);
                    break;
                case ("day") :
                    date = DateUtils.addDays(new Date(), -1);
                    break;
            }
            String dateString = format.format( date);

            value = type.equalsIgnoreCase("month") ? dateString.substring(0, 7) : dateString;
        }
        return getMeterResult(params.get("meter"), type, value);
    }
    private String getMeterResult(String meter, String type, String value) {
       logger.info(" MeterResult for " + meter + " " + type + " " + value);
       return restClient.getJsonResult(Energy_Url,
               jsonService.getMeterResultJson(meter, type, value).toString());
    }

}
