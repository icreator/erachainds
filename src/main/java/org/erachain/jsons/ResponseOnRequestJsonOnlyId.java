package org.erachain.jsons;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseOnRequestJsonOnlyId {

    private String tx;

    private long date;

    //    private int partNo;
    private Integer pos;

    private Integer size;

    public ResponseOnRequestJsonOnlyId(String tx, long date) {
        this.tx = tx;
        this.date = date;
    }


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

    public Integer getPos() {
        return pos;
    }

    public Integer getSize() {
        return size;
    }
}
