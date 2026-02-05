package com.stephenjm.crypto.model;

/**
 * Signing algorithms supported by the KMS client.
 */
public enum SigningAlgorithm {
    /**
     * HMAC with SHA-256
     */
    HMAC_SHA_256("HmacSHA256"),
    
    /**
     * HMAC with SHA-384
     */
    HMAC_SHA_384("HmacSHA384"),
    
    /**
     * HMAC with SHA-512
     */
    HMAC_SHA_512("HmacSHA512");
    
    private final String algorithmName;
    
    SigningAlgorithm(String algorithmName) {
        this.algorithmName = algorithmName;
    }
    
    /**
     * Get the Java algorithm name.
     * 
     * @return algorithm name
     */
    public String getAlgorithmName() {
        return algorithmName;
    }
}
