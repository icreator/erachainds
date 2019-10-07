package org.erachain.service;

import org.apache.flink.api.java.tuple.Tuple2;
import org.erachain.entities.account.Account;
import org.erachain.repositories.AccountProc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DataBeanLoader {

    @Autowired
    private AccountProc accountProc;

    @Autowired
    private List<Tuple2<String, String>> namesObjectsServices;

    private List<Account> accountList;

    public DataBeanLoader() {
        List<Account> accounts = accountProc.getAccounts();
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public List<Tuple2<String, String>> getNamesObjectsServices() {
        return namesObjectsServices;
    }
}
