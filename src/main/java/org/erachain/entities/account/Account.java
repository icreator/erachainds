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

    private int    id;
    private String accountUrl;
    private String publicKey;
    private String privateKey;
    private String recipient;
    private String user;
    private String identityName;
    private String password ;
//    private String type;
//    private Timestamp runDate;
//    private int lastReceived = 0;
//
//    private int lastSubmitted = 0;
//
//    private int lastAccepted = 0;

//    public String getCreator() {
//        return creator;
//    }
//
//    public void setCreator(String creator) {
//        this.creator = creator;
//    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

//    public int getLastReceived() {
//        return lastReceived;
//    }
//
//    public int getLastSubmitted() {
//        return lastSubmitted;
//    }
//
//    public int getLastAccepted() {
//        return lastAccepted;
//    }
//
//    public void addReceived() {
//        lastReceived ++;
//    }
//
//    public void addSubmitted() {
//        lastSubmitted ++;
//    }
//
//    public void addAccepted() {
//        lastAccepted ++;
//    }

//    public void reset() {
//        lastReceived = 0;
//        lastSubmitted = 0;
//        lastAccepted = 0;
//    }

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
//    public boolean checkTime() {
//        if (runDate == null)
//            return true;
//
//        Date date = new Date(runDate.getTime());
//
//        switch (type) {
//            case ("month") :
//                Date incrementedDate = DateUtl.addMonths(date, 1);
//                if (date.after(incrementedDate))
//                    return true;
//                break;
//        }
//        return false;
//    }
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public Timestamp getRunDate() {
//        return runDate;
//    }
//
//    public void setRunDate(Timestamp runDate) {
//        this.runDate = runDate;
//    }

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
