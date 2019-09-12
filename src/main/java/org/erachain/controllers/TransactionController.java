package org.erachain.controllers;


import org.erachain.repositories.DbUtils;
import org.erachain.repositories.InfoSave;
import org.erachain.service.JsonService;
import org.erachain.utils.DateUtl;
import org.erachain.utils.crypto.Base58;
import org.erachain.utils.loggers.LoggableController;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.metadata.Db2CallMetaDataProvider;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/blockchain")
public class TransactionController {

    @Autowired
    private InfoSave infoSave;

    @Autowired
    private DbUtils dbUtils;

    @Autowired
    private DateUtl dateUtl;

    @Autowired
    private JsonService jsonService;

    @Value("${GET_LAST_RECORD_BY_DATE}")
    private String GET_LAST_RECORD_BY_DATE;

    @Value("${GET_LAST_BLOCK_CHAIN_INFO_BY_DATE}")
    private String GET_LAST_BLOCK_CHAIN_INFO_BY_DATE;


    @LoggableController
    @RequestMapping(value = "/proc/{id}", method = RequestMethod.GET, produces = {"text/plain"})
    //         produces = {"text/json", "text/xml"})
    public String getClientData(@PathVariable("id") String ident,
                                @RequestParam List<String> names,
                                @RequestParam List<String> values,
                                @RequestParam(value = "xml", required = false) String xml
    ) throws InterruptedException {

//        byte[] data = Base58.decode(rawDataBase58);

        Map<String, String> params = new HashMap<>();
        int i = 0;
        for (String name : names) {
            params.put(name, values.get(i ++));
        }
        String result = "";
        try {
            result = infoSave.fetchDataForClient(ident, params);
            if ("yes".equalsIgnoreCase(xml)) {
                JSONObject json = new JSONObject(result);
                result = XML.toString(json, ident);
            }
        } catch (Exception e) {
            String message = "check parameters - " + e.getMessage();
            return "{\"error\"=\"" + message + "\"}";
        }
        return result;
    }


    @LoggableController
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {"text/plain"})
    public String getIdentByDate(@PathVariable("id") String ident,
                                @RequestParam(value = "date", required = false)  String date)  throws InterruptedException {


        Map<String, Object> map = null;
        try {
            Date runDate = (date == null ? new Date() : dateUtl.stringToDate(date));
            map = dbUtils.getDataMap(GET_LAST_BLOCK_CHAIN_INFO_BY_DATE, ident, runDate.getTime());
        } catch (Exception e) {
            String message = "check parameters - " + e.getMessage();
            return "{\"error\"=\"" + message + "\"}";
        }
        if (map == null || map.isEmpty())
            return "{\"error\":\"Not found\"}";
        JSONObject jsonObject = jsonService.getJson("response.json");
        return jsonService.setMapToObj(map, jsonObject).toString();
    }

/* Формта возвращаемого значения
        {

            "error" : "Not found"


            date: "timestamp" - дата запроса данных у внешнего сервиса
            tx: "BLOCKNO-TXNO" - номер блока и транзакции в блоке
            pos: 0 - позиция данных в транзакции, необязательный если вся транзакция для одного идентификатора
            size: 122 - длинна данных в символах UTF-8, необязательный если вся транзакция для одного идентификатора

        }
*/

        @LoggableController
    @RequestMapping(value = "/data/{id}", method = RequestMethod.GET, produces = {"text/plain"})
    //         produces = {"text/json", "text/xml"})
    public String getDataByDate(@PathVariable("id") String ident,
                                @RequestParam(value = "date", required = false)  String date)  throws InterruptedException {

        byte[] result = null;
        try {
            Date runDate = (date == null ? new Date() : dateUtl.stringToDate(date));
            result = dbUtils.getData(GET_LAST_RECORD_BY_DATE, ident, runDate.getTime());
        } catch (Exception e) {
            String message = "check parameters - " + e.getMessage();
            return "{\"error\":\"" + message + "\"}";
        }
        return (result == null ? "{\"error\":\"Not found\"}" : new String(result));

    }
//
}


