package com.stephenjm.crypto.model;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Request to encrypt plaintext data.
 */
public class EncryptRequest {
    private final String keyId;
    private final byte[] plaintext;
    private final Map<String, String> encryptionContext;
    
    private EncryptRequest(String keyId, byte[] plaintext, Map<String, String> encryptionContext) {
        this.keyId = Objects.requireNonNull(keyId, "keyId cannot be null");
        this.plaintext = Objects.requireNonNull(plaintext, "plaintext cannot be null");
        this.encryptionContext = encryptionContext;
    }
    
    public String getKeyId() {
        return keyId;
    }
    
    public byte[] getPlaintext() {
        return plaintext;
    }
    
    public Map<String, String> getEncryptionContext() {
        return encryptionContext;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String keyId;
        private byte[] plaintext;
        private Map<String, String> encryptionContext;
        
        public Builder keyId(String keyId) {
            this.keyId = keyId;
            return this;
        }
        
        public Builder plaintext(byte[] plaintext) {
            this.plaintext = plaintext;
            return this;
        }
        
        public Builder encryptionContext(Map<String, String> context) {
            this.encryptionContext = context;
            return this;
        }
        
        public EncryptRequest build() {
            return new EncryptRequest(keyId, plaintext, encryptionContext);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EncryptRequest that = (EncryptRequest) o;
        return Objects.equals(keyId, that.keyId) &&
               Arrays.equals(plaintext, that.plaintext) &&
               Objects.equals(encryptionContext, that.encryptionContext);
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(keyId, encryptionContext);
        result = 31 * result + Arrays.hashCode(plaintext);
        return result;
    }
    
    @Override
    public String toString() {
        return "EncryptRequest{" +
                "keyId='" + keyId + '\'' +
                ", plaintextLength=" + (plaintext != null ? plaintext.length : 0) +
                ", encryptionContext=" + encryptionContext +
                '}';
    }
}
