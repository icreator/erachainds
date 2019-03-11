package org.erachain.entities.account;

import org.erachain.utils.crypto.Base58;

import java.util.Arrays;

public class Account {

    private String accountUrl;
    private String publicKey;
    private String privateKey;
    private String user;
    private int    accountId;
    private String passWord ;

    public void setAccountUrl(String accountUrl) {
        this.accountUrl = accountUrl;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getAccountUrl() {
        return accountUrl;
    }
    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }


}
