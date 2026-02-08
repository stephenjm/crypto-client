package com.crypto.security.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * Response from generate data key operation.
 */
public class GenerateDataKeyResponse {
    private final String keyId;
    private final byte[] plaintextKey;
    private final byte[] ciphertextKey;
    private final KeySpec keySpec;
    
    private GenerateDataKeyResponse(String keyId, byte[] plaintextKey, byte[] ciphertextKey, KeySpec keySpec) {
        this.keyId = Objects.requireNonNull(keyId, "keyId cannot be null");
        this.plaintextKey = Objects.requireNonNull(plaintextKey, "plaintextKey cannot be null");
        this.ciphertextKey = Objects.requireNonNull(ciphertextKey, "ciphertextKey cannot be null");
        this.keySpec = Objects.requireNonNull(keySpec, "keySpec cannot be null");
    }
    
    public String getKeyId() {
        return keyId;
    }
    
    public byte[] getPlaintextKey() {
        return plaintextKey;
    }
    
    public byte[] getCiphertextKey() {
        return ciphertextKey;
    }
    
    public KeySpec getKeySpec() {
        return keySpec;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String keyId;
        private byte[] plaintextKey;
        private byte[] ciphertextKey;
        private KeySpec keySpec;
        
        public Builder keyId(String keyId) {
            this.keyId = keyId;
            return this;
        }
        
        public Builder plaintextKey(byte[] plaintextKey) {
            this.plaintextKey = plaintextKey;
            return this;
        }
        
        public Builder ciphertextKey(byte[] ciphertextKey) {
            this.ciphertextKey = ciphertextKey;
            return this;
        }
        
        public Builder keySpec(KeySpec keySpec) {
            this.keySpec = keySpec;
            return this;
        }
        
        public GenerateDataKeyResponse build() {
            return new GenerateDataKeyResponse(keyId, plaintextKey, ciphertextKey, keySpec);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenerateDataKeyResponse that = (GenerateDataKeyResponse) o;
        return Objects.equals(keyId, that.keyId) &&
               Arrays.equals(plaintextKey, that.plaintextKey) &&
               Arrays.equals(ciphertextKey, that.ciphertextKey) &&
               keySpec == that.keySpec;
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(keyId, keySpec);
        result = 31 * result + Arrays.hashCode(plaintextKey);
        result = 31 * result + Arrays.hashCode(ciphertextKey);
        return result;
    }
    
    @Override
    public String toString() {
        return "GenerateDataKeyResponse{" +
                "keyId='" + keyId + '\'' +
                ", plaintextKeyLength=" + (plaintextKey != null ? plaintextKey.length : 0) +
                ", ciphertextKeyLength=" + (ciphertextKey != null ? ciphertextKey.length : 0) +
                ", keySpec=" + keySpec +
                '}';
    }
}
