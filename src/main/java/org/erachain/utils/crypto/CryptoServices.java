package org.erachain.utils.crypto;

import java.io.IOException;

public interface CryptoServices {

    byte[] digest(byte[] input);
    byte[] doubleDigest(byte[] input);
    Pair<byte[], byte[]> createKeyPair(byte[] seed);
    String getAddressFromShort(byte[] addressShort);
    String getAddress(byte[] publicKey);
    String getATAddress(byte[] signature);
    boolean isValidAddress(String address);
    byte[] sign(Pair account, byte[] message);
    boolean verify(byte[] publicKey, byte[] signature, byte[] message);
    byte[] ripemd160(byte[] publicKey) throws IOException;
}
