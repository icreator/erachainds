package org.erachain.controllers;


import org.erachain.repositories.InfoSave;
import org.erachain.utils.crypto.Base58;
import org.erachain.utils.loggers.LoggableController;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/clientdata")
public class TransactionController {

    @Autowired
    private InfoSave infoSave;

    @LoggableController
    @RequestMapping(value = "/proc/{ident}", method = RequestMethod.GET, produces = {"text/json", "text/xml"})
    public String getClientData(@PathVariable("ident") String ident, @RequestParam("type") String type, @RequestParam("value") String value, @RequestParam(value = "xml", required = false) String xml) throws InterruptedException {

//        byte[] data = Base58.decode(rawDataBase58);

        Map<String, String> params = new HashMap<>();
        params.put("type", type);
        params.put("value", value);
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

}


