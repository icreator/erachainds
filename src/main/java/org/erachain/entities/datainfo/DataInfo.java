package org.erachain.entities.datainfo;

import org.erachain.repositories.DbUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataInfo {

    private static final int TRANS_MAXSIZE = 10000;

    private int id;
    private int accountId;
    private Timestamp runDate;
    private byte[] data;
    private String identity;
    private Timestamp subDate;
//    private String signature;
    private Timestamp accDate;

    private int    actRequestId;
    private Timestamp sendToClientDate;
    private Timestamp acceptClientDate;


    public Timestamp getAcceptClientDate() {
        return acceptClientDate;
    }

    public void setAcceptClientDate(Timestamp acceptClientDate) {
        this.acceptClientDate = acceptClientDate;
    }

    public int getActRequestId() {
        return actRequestId;
    }

    public void setActRequestId(int actRequestId) {
        this.actRequestId = actRequestId;
    }

//    public String getRequestType() {
//        return requestType;
//    }
//
//    public void setRequestType(String requestType) {
//        this.requestType = requestType;
//    }
//
//    public String getRequestValue() {
//        return requestValue;
//    }
//
//    public void setRequestValue(String requestValue) {
//        this.requestValue = requestValue;
//    }

    public Timestamp getSendToClientDate() {
        return sendToClientDate;
    }

    public void setSendToClientDate(Timestamp sendToClientDate) {
        this.sendToClientDate = sendToClientDate;
    }

//    public int getPartNo() {
//        return partNo;
//    }
//
//    public void setPartNo(int partNo) {
//        this.partNo = partNo;
//    }
//
//    public String getSignature() {
//        return signature;
//    }
//
    public void setSignature(String signature, DbUtils dbUtils) {
//        this.signature = signature;
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

    public List<byte[]> getData(DbUtils dbUtils) {
        return split(data);
    }

    public List<DataEra> getDataEras(DbUtils dbUtils) {
        return dbUtils.fetchData(DataEra.class, "DataEra", "dataInfoId = " + id);
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

    private List<byte[]> split(byte[] arrayToCut) {
        int size = TRANS_MAXSIZE;
        return split(arrayToCut, size);
    }
    private List<byte[]> split(byte[] arrayToCut, int size) {
        List<byte[]> list = new ArrayList<>();

        int num = arrayToCut.length <= size ? 0 : arrayToCut.length/size;
        if (num == 0) {
            list.add(arrayToCut);
            return list;
        }
        size =  arrayToCut.length/(num + 1);
        int pos = 0;
        for (int i = 0; i < num; i ++) {
            list.add(Arrays.copyOfRange(arrayToCut, pos, pos + size));
            pos += size;
        }
        list.add(Arrays.copyOfRange(arrayToCut, pos, arrayToCut.length));
        return list;
    }

}
