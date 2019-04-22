package org.erachain.entities.request;

public class Params {

    private int id;
    private int requestId;
    private String paramName;
    private String dataType;
    private String format;
    private String defValue;
    private int curValue;
    public int getCurValue() {
        return curValue;
    }

    public void setCurValue(int curValue) {
        this.curValue = curValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDefValue() {
        return defValue;
    }

    public void setDefValue(String defValue) {
        this.defValue = defValue;
    }

}
