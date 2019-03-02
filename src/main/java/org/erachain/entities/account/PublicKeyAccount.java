package org.erachain.entities.account;

import org.erachain.utils.crypto.Crypto;

public class PublicKeyAccount extends Account{

    protected byte[] publicKey;

    public PublicKeyAccount(byte[] publicKey) {
        super(Crypto.getInstance().getAddress(publicKey));
        this.publicKey = publicKey;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }
}
