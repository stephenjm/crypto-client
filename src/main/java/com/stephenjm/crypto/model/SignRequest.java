package com.stephenjm.crypto.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * Request to sign a message.
 */
public class SignRequest {
    private final String keyId;
    private final byte[] message;
    private final SigningAlgorithm signingAlgorithm;
    
    private SignRequest(String keyId, byte[] message, SigningAlgorithm signingAlgorithm) {
        this.keyId = Objects.requireNonNull(keyId, "keyId cannot be null");
        this.message = Objects.requireNonNull(message, "message cannot be null");
        this.signingAlgorithm = signingAlgorithm != null ? signingAlgorithm : SigningAlgorithm.HMAC_SHA_256;
    }
    
    public String getKeyId() {
        return keyId;
    }
    
    public byte[] getMessage() {
        return message;
    }
    
    public SigningAlgorithm getSigningAlgorithm() {
        return signingAlgorithm;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String keyId;
        private byte[] message;
        private SigningAlgorithm signingAlgorithm;
        
        public Builder keyId(String keyId) {
            this.keyId = keyId;
            return this;
        }
        
        public Builder message(byte[] message) {
            this.message = message;
            return this;
        }
        
        public Builder signingAlgorithm(SigningAlgorithm signingAlgorithm) {
            this.signingAlgorithm = signingAlgorithm;
            return this;
        }
        
        public SignRequest build() {
            return new SignRequest(keyId, message, signingAlgorithm);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignRequest that = (SignRequest) o;
        return Objects.equals(keyId, that.keyId) &&
               Arrays.equals(message, that.message) &&
               signingAlgorithm == that.signingAlgorithm;
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(keyId, signingAlgorithm);
        result = 31 * result + Arrays.hashCode(message);
        return result;
    }
    
    @Override
    public String toString() {
        return "SignRequest{" +
                "keyId='" + keyId + '\'' +
                ", messageLength=" + (message != null ? message.length : 0) +
                ", signingAlgorithm=" + signingAlgorithm +
                '}';
    }
}
