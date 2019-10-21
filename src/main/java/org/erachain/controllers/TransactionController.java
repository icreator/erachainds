package org.erachain.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.erachain.jsons.ListResponseOnRequestJsonOnlyId;
import org.erachain.jsons.ResponseOnRequestJsonOnlyId;
import org.erachain.repositories.InfoSave;
import org.erachain.utils.DateUtl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/v1/blockchain")
public class TransactionController {

    @Autowired
    private InfoSave infoSave;

    @Autowired
    private DateUtl dateUtl;

    @Autowired
    private Logger logger;

    /**
     * Формта возвращаемого значения
     * {
     * "error" : "Not found"
     * <p>
     * date: "timestamp" - дата запроса данных у внешнего сервиса
     * tx: "BLOCKNO-TXNO" - номер блока и транзакции в блоке
     * pos: 0 - позиция данных в транзакции, необязательный если вся транзакция для одного идентификатора
     * size: 122 - длинна данных в байтах, необязательный если вся транзакция для одного идентификатора
     * }
     *
     * @param ident
     * @param names
     * @param values
     * @param date
     * @param principal
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("permitAll()")
    public String getIdentifierByDate(@PathVariable("id") String ident,
                                      @RequestParam(required = false) List<String> names,
                                      @RequestParam(required = false) List<String> values,
                                      @RequestParam(value = "date", required = false) String date,
                                      Principal principal) throws JsonProcessingException {
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
            result = infoSave.fetchDataByIdLastBlockDataParams(ident, params, runDate.getTime(), principal.getName());
        } catch (Exception e) {
            return "{\"error\"=\"" + e.getMessage() + "\"}";
        }
        ObjectMapper mapper = new ObjectMapper();
        if (result.isPresent()) {
            return mapper.writeValueAsString(result.get());
        }
        return "{\"error\" : \"" + "Not found" + "\"}";
    }


    @RequestMapping(value = "/{id}/data", method = RequestMethod.GET, produces = {"text/plain"})
    @PreAuthorize("permitAll()")
    public String getDataByDate(@PathVariable("id") String ident,
                                @RequestParam(required = false) List<String> names,
                                @RequestParam(required = false) List<String> values,
                                @RequestParam(value = "date", required = false) String date,
                                Principal principal) throws JsonProcessingException {
        Optional<String> result;
        Map<String, String> params = new HashMap<>();
        int i = 0;
        if (names != null) {
            for (String name : names) {
                params.put(name, values.get(i++));
            }
        }
        try {
            Date runDate = (date == null ? new Date() : dateUtl.stringToDate(date));
            result = infoSave.fetchDataByIdDataLastBlockDataParams(ident, params, runDate.getTime(), principal.getName());
        } catch (Exception e) {
            return "{\"error\"=\"" + e.getMessage() + "\"}";
        }
        return result.orElse("{\"error\" : \"" + "Not found" + "\"}");

    }

    @RequestMapping(value = "/{id}/history", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("permitAll()")
    public String getIdentHistoryByDate(@PathVariable("id") String ident,
                                        @RequestParam(required = false) List<String> names,
                                        @RequestParam(required = false) List<String> values,
                                        @RequestParam(value = "date", required = false) String date,
                                        @RequestParam(value = "limit", required = false) String limit,
                                        Principal principal) throws JsonProcessingException {
        Optional<ListResponseOnRequestJsonOnlyId> result;
        Map<String, String> params = new HashMap<>();
        int i = 0;
        if (names != null) {
            for (String name : names) {
                params.put(name, values.get(i++));
            }
        }
        try {
            Date runDate = (date == null ? new Date() : dateUtl.stringToDate(date));
            int lim = (limit == null ? 50 : Integer.parseInt(limit));
            result = infoSave.fetchDataByIdHistoryBlockDataParams(ident, params, runDate.getTime(), lim, principal.getName());
        } catch (Exception e) {
            return "{\"error\"=\"" + e.getMessage() + "\"}";
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(result.orElseThrow(IllegalArgumentException::new));
    }
}


