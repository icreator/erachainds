package org.erachain.service.eraService;

import org.erachain.entities.account.Account;
import org.erachain.entities.datainfo.DataEra;
import org.erachain.entities.datainfo.DataInfo;
import org.erachain.repositories.DbUtils;
import org.erachain.service.RestClient;
import org.erachain.service.energetik.JsonService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@PropertySource("classpath:custom.properties")
public class EraClient {
    @Autowired
    private Logger logger;

    @Value("${EraService_Url}")
    private String EraService_Url;

    @Value("${EraService_creator}")
    private String EraService_creator;

    @Value("${EraService_Url_Signature}")
    private String EraService_Url_Signature;

    @Value("${EraService_password}")
    private String EraService_password;

    @Value("${EraService_title}")
    private String EraService_title;

    @Autowired
    private JsonService jsonService;

    @Autowired
    private DbUtils dbUtils;

    private RestClient restClient;

    @Autowired
    public EraClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public void setSignature(DataInfo dataInfo, Account account) throws Exception {
//        String data = new String(dataInfo.getData());

        List<DataEra> dataEras = new ArrayList<>();
        for (byte[] dt : dataInfo.getData(dbUtils)) {
            String data = new String(dt);
            DataEra dataEra = new DataEra();
            dataEra.setDataInfoId(dataInfo.getId());
            String[] urlParams = {EraService_creator, account.getRecipient()};
            Map<String, String> params = new HashMap<>();
            params.put("password", EraService_password);
            params.put("title", EraService_title);
            String url = restClient.addParams(EraService_Url, urlParams, params);
            logger.info(" url " + url);
            String result = null;
            try {
                result = restClient.getResult(url, data);
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new Exception(EraService_Url + " " + e.getMessage());
            }
            String signature = jsonService.getValue(result, "signature");
            logger.info(" signature " + signature);
            dataEra.setSignature(signature);
            dataEras.add(dataEra);
        }
        int partNo = 0;
        for (DataEra dataEra : dataEras) {
            dataEra.setPartNo(partNo ++);
            dbUtils.setDbObj(dataEra, "DataEra");
        }
    }

    public String checkChain(DataEra dataEra) throws Exception {
        String signature = dataEra.getSignature();
        String result = null;
        try {
            result = restClient.getResult(EraService_Url_Signature + "/" + signature);
        } catch (Exception e) {
         //   logger.error(e.getMessage());
            throw new Exception(EraService_Url_Signature + " " + e.getMessage());

        }
        return result;
    }
}