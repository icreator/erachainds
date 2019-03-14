package org.erachain.service.energetik;

import org.erachain.service.RestClient;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class ClientEnergy {
    @Autowired
    private Logger logger;

    @Autowired RestClient restClient;

    @Autowired JsonService jsonService;

//    @Value("Energy_Url")
    private String Energy_Url;

    String sessionId;

    public void ClientLogin(Map<String, String> params)  {
        ClientLogin(params.get("url"),
                params.get("user"), params.get("password"));
        params.put("sessionId", sessionId);
    }
    private void ClientLogin(String url, String user, String pass) {
        logger.info(" login " + url + " " + user);
        String json = restClient.getJsonResult(url, jsonService.getAuth(user, pass).toString());
        sessionId = jsonService.getValue(json, "result");
        restClient.addHeader("X-Session-Id", sessionId);
        Energy_Url = url;
        logger.info("end login " + sessionId);
    }

    public  List<String> getNetWorkList() {
        logger.info(" NetWorkList ");
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
        return getMeterResult(params.get("meter"), params.get("type"), params.get("value"));
    }
    private String getMeterResult(String meter, String type, String value) {
        logger.info(" MeterResult for " + meter + " " + type + " " + value);
       String response = restClient.getJsonResult(Energy_Url,
               jsonService.getMeterResultJson(meter, type, value).toString());
        return response;
    }

}
