package org.erachain.entities.account;

import org.apache.commons.lang3.time.DateUtils;
import org.erachain.repositories.DbUtils;
import org.erachain.utils.crypto.Base58;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Account {

    // db
    private int    id;
    private String accountUrl;
    private String publicKey;
    private String privateKey;
    private String recipient;
    private String user;
    private String identityName;
    private String password ;
    // end db

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }



    public String getIdentityName() {
        return identityName;
    }

    public void setIdentityName(String identityName) {
        this.identityName = identityName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    private Map<String, String> params = new HashMap<>();

    public Map<String, String> getParams() {
        params.put("accountUrl", accountUrl);
        params.put("user", user);
        params.put("password", password);
        params.put("identityName", identityName);

//        params.put("type", type);
        return params;
    }


    public void setAccountUrl(String accountUrl) {
        this.accountUrl = accountUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAccountUrl() {
        return accountUrl;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
