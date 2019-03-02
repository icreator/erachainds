package org.erachain.entities.transactions;

import org.erachain.entities.account.Account;
import org.erachain.entities.account.PublicKeyAccount;
import org.erachain.repositories.TransactionTypeGetter;
import org.erachain.utils.TransUtil;
import org.erachain.utils.crypto.Base58;
import org.erachain.utils.crypto.Crypto;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Transaction {

    @Autowired
    private TransUtil transUtil;

    @Autowired
    private TransactionTypeGetter typeGetter;

    protected Long reference = 0L;
    protected BigDecimal fee = BigDecimal.ZERO;
    protected byte[] feePow;
    protected byte[] signature;

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }

    private boolean isOk = true;
    private String errMessage;

    public void setSignatureLength(int signatureLength) {
        this.signatureLength = signatureLength;
    }

    public void setSignaturePosition(int signaturePosition) {
        this.signaturePosition = signaturePosition;
    }

    public int getSignatureLength() {
        return signatureLength;
    }

    public int getSignaturePosition() {
        return signaturePosition;
    }

    protected int signatureLength;
    protected int signaturePosition;
    protected long timestamp;

    protected byte[] typeBytes;
    protected byte[] data; // trans data

    public void setRowData(byte[] rowData) {
        this.rowData = rowData;
    }

    public byte[] getRowData() {
        return rowData;
    }

    protected byte[] rowData; // trans rowdata
    protected Account recipient;
    protected BigDecimal amount;
    protected long key;
    protected PublicKeyAccount creator;
    protected int transType;
    protected Header header;

    private Map<String, Object> fldMap = new HashMap<>();

    public void setFldByName(String fldName, Object value) {
        fldMap.put(fldName.toUpperCase(), value);
    }

    public  <T> T getFldByName(String fldName) {
        return (T) fldMap.get(fldName.toUpperCase());
    }

    public Header getHeader() {
        return header;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Account getRecipient() {
        return recipient;
    }

    public byte[] getSignature() {
        return signature;
    }


    public PublicKeyAccount getCreator() {
        return creator;
    }

    public static class Header {

        byte[] amount;
        long key;
        byte[] arbitraryData;
        byte[] encrypted;
        byte[] isText;
        String header;

        public Header(byte[] amount, long key, String header, byte[] arbitrarydata, byte[] encrypted, byte[] isText) {

            this.amount = amount;
            this.key = key;
            this.arbitraryData = arbitrarydata;
            this.encrypted = encrypted;
            this.isText = isText;
            this.header = header;

        }
    }

    public Transaction() {
    }

    public Transaction(byte[] data) {

        this.data = data;
        typeBytes = Arrays.copyOfRange(data, 0, 4);
        transType = Byte.toUnsignedInt(typeBytes[0]);
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {

        return "\nTransaction{" +
                //"reference=" + reference + ",\n" +
//                "fee=" + this.fee + ",\n" +
//                "feePow=" + this.feePow + ",\n" +
                "timestamp=" + timestamp + ",\n" +
                "TypeId=" + typeBytes[0] + ",\n" +
                //"Data=" + Base58.encode(getHeader().arbitraryData) + ",\n" +
                //"encrypted=" + Arrays.toString(getHeader().encrypted) + ",\n" +
                "Public Key: =" + Base58.encode(getCreator().getPublicKey()) + ",\n" +
                "creator=" + Crypto.getInstance().getAddress(getCreator().getPublicKey()) + ",\n" +
                "signature=" + Base58.encode(signature) + ",\n" +
                '}';
    }

}
