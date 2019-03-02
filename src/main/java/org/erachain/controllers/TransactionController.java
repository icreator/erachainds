package org.erachain.controllers;

import org.erachain.entities.TransactionFactory;
import org.erachain.entities.transactions.Transaction;
import org.erachain.repositories.DBTransaction;
import org.erachain.utils.loggers.LoggableController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//@Controller
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private DBTransaction transProc;

    @Autowired
    private TransactionFactory transactionFactory;

    @LoggableController
    @PostMapping(path = "/proc")
    public ResponseEntity<String> createTransaction(@RequestParam("trans_token") String rawDataBase58) throws InterruptedException {

        Transaction transaction  = transactionFactory.parse(rawDataBase58);

        if (transaction == null){
            return ResponseEntity.ok("{ \"error message\" : \"Invalid signature\"}");
        }
        else if (!transaction.isOk()) {
            return ResponseEntity.ok("{ \"error message\": \"" + transaction.getErrMessage() + "\"}");
        }

        transProc.setTransaction(transaction);
        return ResponseEntity.ok("ok done");
    }

}


