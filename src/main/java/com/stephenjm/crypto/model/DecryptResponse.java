package com.stephenjm.crypto.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * Response from decrypt operation.
 */
public class DecryptResponse {
    private final String keyId;
    private final byte[] plaintext;
    private final String encryptionAlgorithm;
    
    private DecryptResponse(String keyId, byte[] plaintext, String encryptionAlgorithm) {
        this.keyId = Objects.requireNonNull(keyId, "keyId cannot be null");
        this.plaintext = Objects.requireNonNull(plaintext, "plaintext cannot be null");
        this.encryptionAlgorithm = encryptionAlgorithm;
    }
    
    public String getKeyId() {
        return keyId;
    }
    
    public byte[] getPlaintext() {
        return plaintext;
    }
    
    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String keyId;
        private byte[] plaintext;
        private String encryptionAlgorithm;
        
        public Builder keyId(String keyId) {
            this.keyId = keyId;
            return this;
        }
        
        public Builder plaintext(byte[] plaintext) {
            this.plaintext = plaintext;
            return this;
        }
        
        public Builder encryptionAlgorithm(String encryptionAlgorithm) {
            this.encryptionAlgorithm = encryptionAlgorithm;
            return this;
        }
        
        public DecryptResponse build() {
            return new DecryptResponse(keyId, plaintext, encryptionAlgorithm);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DecryptResponse that = (DecryptResponse) o;
        return Objects.equals(keyId, that.keyId) &&
               Arrays.equals(plaintext, that.plaintext) &&
               Objects.equals(encryptionAlgorithm, that.encryptionAlgorithm);
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(keyId, encryptionAlgorithm);
        result = 31 * result + Arrays.hashCode(plaintext);
        return result;
    }
    
    @Override
    public String toString() {
        return "DecryptResponse{" +
                "keyId='" + keyId + '\'' +
                ", plaintextLength=" + (plaintext != null ? plaintext.length : 0) +
                ", encryptionAlgorithm='" + encryptionAlgorithm + '\'' +
                '}';
    }
}
