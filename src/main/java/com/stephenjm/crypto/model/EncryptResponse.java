package com.stephenjm.crypto.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * Response from encrypt operation.
 */
public class EncryptResponse {
    private final String keyId;
    private final byte[] ciphertext;
    private final String encryptionAlgorithm;
    
    private EncryptResponse(String keyId, byte[] ciphertext, String encryptionAlgorithm) {
        this.keyId = Objects.requireNonNull(keyId, "keyId cannot be null");
        this.ciphertext = Objects.requireNonNull(ciphertext, "ciphertext cannot be null");
        this.encryptionAlgorithm = encryptionAlgorithm;
    }
    
    public String getKeyId() {
        return keyId;
    }
    
    public byte[] getCiphertext() {
        return ciphertext;
    }
    
    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String keyId;
        private byte[] ciphertext;
        private String encryptionAlgorithm;
        
        public Builder keyId(String keyId) {
            this.keyId = keyId;
            return this;
        }
        
        public Builder ciphertext(byte[] ciphertext) {
            this.ciphertext = ciphertext;
            return this;
        }
        
        public Builder encryptionAlgorithm(String encryptionAlgorithm) {
            this.encryptionAlgorithm = encryptionAlgorithm;
            return this;
        }
        
        public EncryptResponse build() {
            return new EncryptResponse(keyId, ciphertext, encryptionAlgorithm);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EncryptResponse that = (EncryptResponse) o;
        return Objects.equals(keyId, that.keyId) &&
               Arrays.equals(ciphertext, that.ciphertext) &&
               Objects.equals(encryptionAlgorithm, that.encryptionAlgorithm);
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(keyId, encryptionAlgorithm);
        result = 31 * result + Arrays.hashCode(ciphertext);
        return result;
    }
    
    @Override
    public String toString() {
        return "EncryptResponse{" +
                "keyId='" + keyId + '\'' +
                ", ciphertextLength=" + (ciphertext != null ? ciphertext.length : 0) +
                ", encryptionAlgorithm='" + encryptionAlgorithm + '\'' +
                '}';
    }
}
