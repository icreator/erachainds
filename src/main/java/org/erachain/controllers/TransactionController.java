package org.erachain.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.erachain.jsons.ResponseOnRequestJsonOnlyId;
import org.erachain.repositories.DbUtils;
import org.erachain.repositories.InfoSave;
import org.erachain.service.JsonService;
import org.erachain.utils.DateUtl;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

//    @RequestMapping(value = "/{id}/proc", method = RequestMethod.GET, produces = {"text/plain"})
//    @PreAuthorize("permitAll()")
//    public String getClientData(@PathVariable("id") String ident,
//                                @RequestParam List<String> names,
//                                @RequestParam List<String> values,
//                                @RequestParam(value = "xml", required = false) String xml
//    ) {
//        Map<String, String> params = new HashMap<>();
//        int i = 0;
//        for (String name : names) {
//            params.put(name, values.get(i++));
//        }
//        String result;
//        try {
//            result = infoSave.fetchDataForClient(ident, params);
//            if ("yes".equalsIgnoreCase(xml)) {
//                JSONObject json = new JSONObject(result);
//                result = XML.toString(json, ident);
//            }
//        } catch (Exception e) {
//            String message = "check parameters - " + e.getMessage();
//            return "{\"error\"=\"" + message + "\"}";
//        }
//        return result;
//    }


/* Формта возвращаемого значения
        {
            "error" : "Not found"

            date: "timestamp" - дата запроса данных у внешнего сервиса
            tx: "BLOCKNO-TXNO" - номер блока и транзакции в блоке
            pos: 0 - позиция данных в транзакции, необязательный если вся транзакция для одного идентификатора
            size: 122 - длинна данных в байтах, необязательный если вся транзакция для одного идентификатора
        }
*/

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("permitAll()")
    public String getIdentifierByDate(@PathVariable("id") String ident,
                                      @RequestParam(required = false) List<String> names,
                                      @RequestParam(required = false) List<String> values,
                                      @RequestParam(value = "date", required = false) String date) throws JsonProcessingException {
        Optional<ResponseOnRequestJsonOnlyId> result;
        Map<String, String> params = new HashMap<>();
        int i = 0;
        if (names != null) {
            for (String name : names) {
                params.put(name, values.get(i++));
            }
        }
        try {
            Date runDate = (date == null ? new Date() : dateUtl.stringToDate(date));
            int limit = 1;
            result = infoSave.fetchDataLastBlockDataParams(ident, params, runDate.getTime(), limit);
        } catch (Exception e) {
            return "{\"error\"=\"" + ("check parameters - " + e.getMessage()) + "\"}";
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(result.orElseThrow(IllegalArgumentException::new));
    }


    @RequestMapping(value = "/{id}/data", method = RequestMethod.GET, produces = {"text/plain"})
    @PreAuthorize("permitAll()")
    public String getDataByDate(@PathVariable("id") String ident,
                                @RequestParam(value = "date", required = false) String date) {

        byte[] result;
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
    @PreAuthorize("permitAll()")
    public String getIdentHistoryByDate(@PathVariable("id") String ident,
                                        @RequestParam(value = "date", required = false) String date,
                                        @RequestParam(value = "limit", required = false) String limit) {
        JSONObject result;
        try {
            Date runDate = (date == null ? new Date() : dateUtl.stringToDate(date));
            int lim = (limit == null ? 50 : Integer.parseInt(limit));
            List<Map<String, Object>> list = dbUtils.getDataMapList(GET_LAST_BLOCK_CHAIN_INFO_BY_DATE, ident, runDate.getTime(), lim);
            if (list == null || list.isEmpty())
                return "{\"error\":\"Not found\"}";
            result = jsonService.getDataMapList(list);
        } catch (Exception e) {
            String message = "check parameters - " + e.getMessage();
            return "{\"error\"=\"" + message + "\"}";
        }
        return result.toString();
    }
}


