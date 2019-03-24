package org.erachain.entities.datainfo;

import java.sql.Timestamp;

public class DataInfo {

    private int id;
    private int accountId;
    private Timestamp runDate;
    private byte[] data;
    private String identity;
    private Timestamp subDate;
    private String signature;
    private Timestamp accDate;
    private int blockId;
    private int transId;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public Timestamp getRunDate() {
        return runDate;
    }

    public void setRunDate(Timestamp runDate) {
        this.runDate = runDate;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public Timestamp getSubDate() {
        return subDate;
    }

    public void setSubDate(Timestamp subDate) {
        this.subDate = subDate;
    }

    public Timestamp getAccDate() {
        return accDate;
    }

    public void setAccDate(Timestamp accDate) {
        this.accDate = accDate;
    }

    public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    public int getTransId() {
        return transId;
    }

    public void setTransId(int transId) {
        this.transId = transId;
    }


}
