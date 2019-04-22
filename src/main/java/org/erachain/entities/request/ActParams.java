package org.erachain.entities.request;


public class ActParams {

    // db
    private int id;
    private int actRequestId;
    private String paramName;
    private String paramValue;
    // end db
    public int getActRequestId() {
        return actRequestId;
    }

    public void setActRequestId(int actRequestId) {
        this.actRequestId = actRequestId;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

}
