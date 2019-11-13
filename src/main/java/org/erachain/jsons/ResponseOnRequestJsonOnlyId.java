package org.erachain.jsons;

public class ResponseOnRequestJsonOnlyId {

    private String tx;

    private long date;

//    private int partNo;

    private int pos;

    private int size;

    public ResponseOnRequestJsonOnlyId(String tx, long date, int pos, int size) {
        this.tx = tx;
        this.date = date;
        this.pos = pos;
        this.size = size;
    }

    public ResponseOnRequestJsonOnlyId(String tx, long date, int partNo, int pos, int size) {
        this.tx = tx;
        this.date = date;
//        this.partNo = partNo;
        this.pos = pos;
        this.size = size;
    }

    public String getTx() {
        return tx;
    }

    public long getDate() {
        return date;
    }

//    public int getPartNo() {
//        return partNo;
//    }

    public int getPos() {
        return pos;
    }

    public int getSize() {
        return size;
    }
}
