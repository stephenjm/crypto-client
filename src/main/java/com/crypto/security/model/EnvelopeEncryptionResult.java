package com.crypto.security.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * Result of envelope encryption operation.
 */
public class EnvelopeEncryptionResult {
    private final byte[] ciphertext;
    private final byte[] encryptedDataKey;
    private final String masterKeyId;
    
    private EnvelopeEncryptionResult(byte[] ciphertext, byte[] encryptedDataKey, String masterKeyId) {
        this.ciphertext = Objects.requireNonNull(ciphertext, "ciphertext cannot be null");
        this.encryptedDataKey = Objects.requireNonNull(encryptedDataKey, "encryptedDataKey cannot be null");
        this.masterKeyId = Objects.requireNonNull(masterKeyId, "masterKeyId cannot be null");
    }
    
    public byte[] getCiphertext() {
        return ciphertext;
    }
    
    public byte[] getEncryptedDataKey() {
        return encryptedDataKey;
    }
    
    public String getMasterKeyId() {
        return masterKeyId;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private byte[] ciphertext;
        private byte[] encryptedDataKey;
        private String masterKeyId;
        
        public Builder ciphertext(byte[] ciphertext) {
            this.ciphertext = ciphertext;
            return this;
        }
        
        public Builder encryptedDataKey(byte[] encryptedDataKey) {
            this.encryptedDataKey = encryptedDataKey;
            return this;
        }
        
        public Builder masterKeyId(String masterKeyId) {
            this.masterKeyId = masterKeyId;
            return this;
        }
        
        public EnvelopeEncryptionResult build() {
            return new EnvelopeEncryptionResult(ciphertext, encryptedDataKey, masterKeyId);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnvelopeEncryptionResult that = (EnvelopeEncryptionResult) o;
        return Arrays.equals(ciphertext, that.ciphertext) &&
               Arrays.equals(encryptedDataKey, that.encryptedDataKey) &&
               Objects.equals(masterKeyId, that.masterKeyId);
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(masterKeyId);
        result = 31 * result + Arrays.hashCode(ciphertext);
        result = 31 * result + Arrays.hashCode(encryptedDataKey);
        return result;
    }
    
    @Override
    public String toString() {
        return "EnvelopeEncryptionResult{" +
                "ciphertextLength=" + (ciphertext != null ? ciphertext.length : 0) +
                ", encryptedDataKeyLength=" + (encryptedDataKey != null ? encryptedDataKey.length : 0) +
                ", masterKeyId='" + masterKeyId + '\'' +
                '}';
    }
}
