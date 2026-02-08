package com.crypto.security.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * Request to verify a signature.
 */
public class VerifyRequest {
    private final String keyId;
    private final byte[] message;
    private final byte[] signature;
    private final SigningAlgorithm signingAlgorithm;
    
    private VerifyRequest(String keyId, byte[] message, byte[] signature, SigningAlgorithm signingAlgorithm) {
        this.keyId = Objects.requireNonNull(keyId, "keyId cannot be null");
        this.message = Objects.requireNonNull(message, "message cannot be null");
        this.signature = Objects.requireNonNull(signature, "signature cannot be null");
        this.signingAlgorithm = signingAlgorithm != null ? signingAlgorithm : SigningAlgorithm.HMAC_SHA_256;
    }
    
    public String getKeyId() {
        return keyId;
    }
    
    public byte[] getMessage() {
        return message;
    }
    
    public byte[] getSignature() {
        return signature;
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
        private byte[] signature;
        private SigningAlgorithm signingAlgorithm;
        
        public Builder keyId(String keyId) {
            this.keyId = keyId;
            return this;
        }
        
        public Builder message(byte[] message) {
            this.message = message;
            return this;
        }
        
        public Builder signature(byte[] signature) {
            this.signature = signature;
            return this;
        }
        
        public Builder signingAlgorithm(SigningAlgorithm signingAlgorithm) {
            this.signingAlgorithm = signingAlgorithm;
            return this;
        }
        
        public VerifyRequest build() {
            return new VerifyRequest(keyId, message, signature, signingAlgorithm);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerifyRequest that = (VerifyRequest) o;
        return Objects.equals(keyId, that.keyId) &&
               Arrays.equals(message, that.message) &&
               Arrays.equals(signature, that.signature) &&
               signingAlgorithm == that.signingAlgorithm;
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(keyId, signingAlgorithm);
        result = 31 * result + Arrays.hashCode(message);
        result = 31 * result + Arrays.hashCode(signature);
        return result;
    }
    
    @Override
    public String toString() {
        return "VerifyRequest{" +
                "keyId='" + keyId + '\'' +
                ", messageLength=" + (message != null ? message.length : 0) +
                ", signatureLength=" + (signature != null ? signature.length : 0) +
                ", signingAlgorithm=" + signingAlgorithm +
                '}';
    }
}
