package org.erachain.controllers;


import org.erachain.utils.crypto.Base58;
import org.erachain.utils.loggers.LoggableController;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @LoggableController
    @RequestMapping(value = "/proc", method = RequestMethod.PUT, produces = "application/json")
    public boolean createTransaction(@RequestParam("trans_token") String rawDataBase58) throws InterruptedException {

        byte[] data = Base58.decode(rawDataBase58);

        return true;
    }

}


