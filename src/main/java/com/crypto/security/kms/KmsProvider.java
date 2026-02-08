package com.crypto.security.kms;

public interface KmsProvider {
    DataKey generateDataKey(String keyId, int keySize) throws Exception;
    byte[] encryptWithKms(String keyId, byte[] plaintext) throws Exception;
}
