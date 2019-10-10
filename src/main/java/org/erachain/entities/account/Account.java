package org.erachain.entities.account;

import java.util.HashMap;
import java.util.Map;

public class Account {

    // db
    private int    id;
    private String accountUrl;
    private String accountRecipient;
    private String recipientPublicKey;
    private String user;
    private String identityName;
    private String password ;
    private String objectName;
    private String idSender;
    // end db

    public String getAccountRecipient() {
        return accountRecipient;
    }

    public void setAccountRecipient(String accountRecipient) {
        this.accountRecipient = accountRecipient;
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

    public String getRecipientPublicKey() {
        return recipientPublicKey;
    }

    public void setRecipientPublicKey(String recipientPublicKey) {
        this.recipientPublicKey = recipientPublicKey;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }
}
