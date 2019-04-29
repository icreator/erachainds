package org.erachain.entities.datainfo;

public class DataEra {

    // db
    private int id;
    private int dataInfoId;
    private String signature;
    private String blockTrId;
    private int partNo;

    private int offset;
    private int lengh;
    // end db


    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLengh() {
        return lengh;
    }

    public void setLengh(int lengh) {
        this.lengh = lengh;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDataInfoId() {
        return dataInfoId;
    }

    public void setDataInfoId(int dataInfoId) {
        this.dataInfoId = dataInfoId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getBlockTrId() {
        return blockTrId;
    }

    public void setBlockTrId(String blockTrId) {
        this.blockTrId = blockTrId;
    }

    public int getPartNo() {
        return partNo;
    }

    public void setPartNo(int partNo) {
        this.partNo = partNo;
    }

}
