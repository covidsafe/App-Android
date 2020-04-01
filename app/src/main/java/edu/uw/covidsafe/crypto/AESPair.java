package edu.uw.covidsafe.crypto;

import javax.crypto.SecretKey;

public class AESPair {
    private SecretKey key;
    private byte[] IV;

    public void setIV(byte[] IV) {
        this.IV = IV;
    }

    public void setKey(SecretKey key) {
        this.key = key;
    }

    public byte[] getIV() {
        return IV;
    }

    public SecretKey getKey() {
        return key;
    }

    public AESPair(SecretKey sk, byte[] ivBytes) {
        key = sk;
        IV = ivBytes;
    }
}
