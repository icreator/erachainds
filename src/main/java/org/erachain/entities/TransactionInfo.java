package org.erachain.entities;

public class TransactionInfo {

    public static final int INVALID_SIGNATURE = 16;

    private String fieldName;
    private String fieldType;
    private int fieldLength;
    private int maxValue;

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getFieldLength() {
        return fieldLength;
    }

    public String getFieldType() { return fieldType; }

    public void setFieldType(String fieldType) { this.fieldType = fieldType; }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setFieldLength(int fieldLength) {
        this.fieldLength = fieldLength;
    }

}
