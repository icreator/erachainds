package org.erachain.service.restclient;

import org.erachain.service.RestClient;
import org.erachain.service.JsonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RestClientTest {

    @Autowired
    private Logger logger;

    @Autowired
    private RestClient restClient;

    @Autowired
    private JsonService jsonService;

    @Value("${EraService_Url}")
    private String EraService_Url;

    @Value("${EraService_Url_Signature}")
    private String EraService_Url_Signature;

    @Value("${EraService_password}")
    private String EraService_password;

    @Value("${EraService_title}")
    private String EraService_title;

    @Test
    public void getConfSign() {
        String signature = "2HruaxXzRWcwHjoAxsgZdYF9JaBJXXqWwuQ1VXonXJbADov9WaFkuCtPwWroeEKTK33G6sYsit3i8Fhzi8MXezW5";
        String result = null;
        try {
            result = restClient.getResult(EraService_Url_Signature + "/" + signature);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        int confirmations = jsonService.getValue(result, "confirmations");
        logger.info(  "confirmations " + confirmations );
    }
}
