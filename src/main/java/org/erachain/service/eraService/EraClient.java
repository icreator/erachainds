package org.erachain.service.eraService;

import org.erachain.entities.account.Account;
import org.erachain.entities.datainfo.DataInfo;
import org.erachain.service.RestClient;
import org.erachain.service.energetik.JsonService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Service
@PropertySource("classpath:custom.properties")
public class EraClient {
    @Autowired
    private Logger logger;

    @Value("${EraService_Url}")
    private String EraService_Url;

    @Value("${EraService_Url_Signature}")
    private String EraService_Url_Signature;

    @Value("${EraService_password}")
    private String EraService_password;

    @Value("${EraService_title}")
    private String EraService_title;

    @Autowired
    private JsonService jsonService;

    private RestClient restClient;

    @Autowired
    public EraClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public void setSignature(DataInfo dataInfo, Account account) {
        String data = new String(dataInfo.getData());
        String[] urlParams = {account.getCreator(), account.getRecipient()};
        Map<String, String> params = new HashMap<>();
        params.put("password", EraService_password);
        params.put("title", EraService_title);
        String url = restClient.addParams(EraService_Url, urlParams, params);
        logger.info(" url " + url);
        String result = restClient.getResult(url, data);
        String signature = jsonService.getValue(result, "signature");
        logger.info(" signature " + signature);
        dataInfo.setSignature(signature);
    }
    public String checkChain(DataInfo dataInfo) {
        String signature = dataInfo.getSignature();
        String result = restClient.getResult(EraService_Url_Signature + "/" + signature);
        return result;
    }
}
