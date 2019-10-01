package org.erachain.entities.account;

import java.util.HashMap;
import java.util.Map;

public class Account {

    // db
    private int    id;
    private String accountUrl;
    private String publicKey;
    private String privateKey;
    private String recipient;
    private String publicKeyRecipient;
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

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKeyRecipient() {
        return publicKeyRecipient;
    }

    public void setPublicKeyRecipient(String publicKeyRecipient) {
        this.publicKeyRecipient = publicKeyRecipient;
    }
}
