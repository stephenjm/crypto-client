package com.crypto.security.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * Response from sign operation.
 */
public class SignResponse {
    private final String keyId;
    private final byte[] signature;
    private final SigningAlgorithm signingAlgorithm;
    
    private SignResponse(String keyId, byte[] signature, SigningAlgorithm signingAlgorithm) {
        this.keyId = Objects.requireNonNull(keyId, "keyId cannot be null");
        this.signature = Objects.requireNonNull(signature, "signature cannot be null");
        this.signingAlgorithm = Objects.requireNonNull(signingAlgorithm, "signingAlgorithm cannot be null");
    }
    
    public String getKeyId() {
        return keyId;
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
        private byte[] signature;
        private SigningAlgorithm signingAlgorithm;
        
        public Builder keyId(String keyId) {
            this.keyId = keyId;
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
        
        public SignResponse build() {
            return new SignResponse(keyId, signature, signingAlgorithm);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignResponse that = (SignResponse) o;
        return Objects.equals(keyId, that.keyId) &&
               Arrays.equals(signature, that.signature) &&
               signingAlgorithm == that.signingAlgorithm;
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(keyId, signingAlgorithm);
        result = 31 * result + Arrays.hashCode(signature);
        return result;
    }
    
    @Override
    public String toString() {
        return "SignResponse{" +
                "keyId='" + keyId + '\'' +
                ", signatureLength=" + (signature != null ? signature.length : 0) +
                ", signingAlgorithm=" + signingAlgorithm +
                '}';
    }
}
