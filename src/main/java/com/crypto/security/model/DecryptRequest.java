package com.crypto.security.model;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Request to decrypt ciphertext data.
 */
public class DecryptRequest {
    private final byte[] ciphertext;
    private final Map<String, String> encryptionContext;
    
    private DecryptRequest(byte[] ciphertext, Map<String, String> encryptionContext) {
        this.ciphertext = Objects.requireNonNull(ciphertext, "ciphertext cannot be null");
        this.encryptionContext = encryptionContext;
    }
    
    public byte[] getCiphertext() {
        return ciphertext;
    }
    
    public Map<String, String> getEncryptionContext() {
        return encryptionContext;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private byte[] ciphertext;
        private Map<String, String> encryptionContext;
        
        public Builder ciphertext(byte[] ciphertext) {
            this.ciphertext = ciphertext;
            return this;
        }
        
        public Builder encryptionContext(Map<String, String> context) {
            this.encryptionContext = context;
            return this;
        }
        
        public DecryptRequest build() {
            return new DecryptRequest(ciphertext, encryptionContext);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DecryptRequest that = (DecryptRequest) o;
        return Arrays.equals(ciphertext, that.ciphertext) &&
               Objects.equals(encryptionContext, that.encryptionContext);
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(encryptionContext);
        result = 31 * result + Arrays.hashCode(ciphertext);
        return result;
    }
    
    @Override
    public String toString() {
        return "DecryptRequest{" +
                "ciphertextLength=" + (ciphertext != null ? ciphertext.length : 0) +
                ", encryptionContext=" + encryptionContext +
                '}';
    }
}
