package org.erachain.entities.account;

import org.erachain.utils.crypto.Base58;

import java.util.Arrays;

public class Account {

    protected byte[] bytes;
    protected String address;
    protected byte[] shortBytes;
    private int accountId;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAddress() {
        return address;
    }

    public Account(String address) {
        this.bytes = Base58.decode(address);
        this.shortBytes = Arrays.copyOfRange(bytes, 1, bytes.length - 4);
        this.address = address;
    }

}
