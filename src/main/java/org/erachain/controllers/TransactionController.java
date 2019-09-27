package org.erachain.controllers;


import org.erachain.repositories.DbUtils;
import org.erachain.repositories.InfoSave;
import org.erachain.service.JsonService;
import org.erachain.utils.DateUtl;
import org.erachain.utils.crypto.Base58;
import org.erachain.utils.loggers.LoggableController;

import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
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

    @Autowired
    private Logger logger;

    @Value("${GET_LAST_RECORD_BY_DATE}")
    private String GET_LAST_RECORD_BY_DATE;

    @Value("${GET_LAST_BLOCK_CHAIN_INFO_BY_DATE}")
    private String GET_LAST_BLOCK_CHAIN_INFO_BY_DATE;

//    @LoggableController
    @RequestMapping(value = "/{id}/proc", method = RequestMethod.GET, produces = {"text/plain"})
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


//    @LoggableController
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public String getIdentByDate(@PathVariable("id") String ident,
                                @RequestParam(value = "date", required = false)  String date)  throws InterruptedException {

        JSONObject jsonObject = null;
        Map<String, Object> map = null;
        try {
            Date runDate = (date == null ? new Date() : dateUtl.stringToDate(date));
            map = dbUtils.getDataMap(GET_LAST_BLOCK_CHAIN_INFO_BY_DATE, ident, runDate.getTime(), 1);
            if (map == null || map.isEmpty())
                return "{\"error\":\"Not found\"}";
             jsonObject = jsonService.getDataMap(map);
        } catch (Exception e) {
            String message = "check parameters - " + e.getMessage();
            return "{\"error\"=\"" + message + "\"}";
        }

        return jsonObject.toString();
    }

/* Формта возвращаемого значения
        {

            "error" : "Not found"


            date: "timestamp" - дата запроса данных у внешнего сервиса
            tx: "BLOCKNO-TXNO" - номер блока и транзакции в блоке
            pos: 0 - позиция данных в транзакции, необязательный если вся транзакция для одного идентификатора
            size: 122 - длинна данных в байтах, необязательный если вся транзакция для одного идентификатора

        }
*/

//        @LoggableController
    @RequestMapping(value = "/{id}/data", method = RequestMethod.GET, produces = {"text/plain"})
    //         produces = {"text/json", "text/xml"})
    public String getDataByDate(@PathVariable("id") String ident,
                                @RequestParam(value = "date", required = false)  String date)  throws InterruptedException {

        byte[] result = null;
        try {
            Date runDate = (date == null ? new Date() : dateUtl.stringToDate(date));
            result = dbUtils.getData(GET_LAST_RECORD_BY_DATE, ident, runDate.getTime(), 1);
        } catch (Exception e) {
            String message = "check parameters - " + e.getMessage();
            return "{\"error\":\"" + message + "\"}";
        }
        return (result == null ? "{\"error\":\"Not found\"}" : new String(result));

    }
    @RequestMapping(value = "/{id}/history", method = RequestMethod.GET, produces = "application/json")
    public String getIdentHistoryByDate(@PathVariable("id") String ident,
                                        @RequestParam(value = "date", required = false)  String date,
                                        @RequestParam(value = "limit", required = false) String limit)  throws InterruptedException {

        List<Map<String, Object>> list = null;
        JSONObject result = null;
        try {
            Date runDate = (date == null ? new Date() : dateUtl.stringToDate(date));
            int lim = (limit == null ? 50 : Integer.parseInt(limit));
            list = dbUtils.getDataMapList(GET_LAST_BLOCK_CHAIN_INFO_BY_DATE, ident, runDate.getTime(), lim);
            if (list == null || list.isEmpty())
                return "{\"error\":\"Not found\"}";
            result = jsonService.getDataMapList(list);
        } catch (Exception e) {
            String message = "check parameters - " + e.getMessage();
            return "{\"error\"=\"" + message + "\"}";
        }
        return result.toString();
    }
//
}


