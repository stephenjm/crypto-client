package com.crypto.security.model;

import java.util.Objects;

/**
 * Request to generate a data encryption key.
 */
public class GenerateDataKeyRequest {
    private final String keyId;
    private final KeySpec keySpec;
    
    private GenerateDataKeyRequest(String keyId, KeySpec keySpec) {
        this.keyId = Objects.requireNonNull(keyId, "keyId cannot be null");
        this.keySpec = Objects.requireNonNull(keySpec, "keySpec cannot be null");
    }
    
    public String getKeyId() {
        return keyId;
    }
    
    public KeySpec getKeySpec() {
        return keySpec;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String keyId;
        private KeySpec keySpec;
        
        public Builder keyId(String keyId) {
            this.keyId = keyId;
            return this;
        }
        
        public Builder keySpec(KeySpec keySpec) {
            this.keySpec = keySpec;
            return this;
        }
        
        public GenerateDataKeyRequest build() {
            return new GenerateDataKeyRequest(keyId, keySpec);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenerateDataKeyRequest that = (GenerateDataKeyRequest) o;
        return Objects.equals(keyId, that.keyId) &&
               keySpec == that.keySpec;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(keyId, keySpec);
    }
    
    @Override
    public String toString() {
        return "GenerateDataKeyRequest{" +
                "keyId='" + keyId + '\'' +
                ", keySpec=" + keySpec +
                '}';
    }
}
