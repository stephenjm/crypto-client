package com.crypto.security.kms;

import java.util.Objects;

public final class DataKey {
    private final String id;
    private final byte[] encryptedKey;
    private final int keyLength;

    public DataKey(String id, byte[] encryptedKey, int keyLength) {
        this.id = Objects.requireNonNull(id);
        this.encryptedKey = encryptedKey == null ? null : encryptedKey.clone();
        this.keyLength = keyLength;
    }

    public String getId() { return id; }
    public byte[] getEncryptedKey() { return encryptedKey == null ? null : encryptedKey.clone(); }
    public int getKeyLength() { return keyLength; }
}
