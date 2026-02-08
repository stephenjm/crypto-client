package com.crypto.spring.model;

/**
 * Business domain enum representing operational states for the crypto client.
 * Defines our unique failure handling strategy based on KMS availability.
 */
public enum FailureMode {
    /**
     * Normal operations: Full encrypt and decrypt capability with KMS access.
     */
    NORMAL("Full operations: encrypt + decrypt"),
    
    /**
     * Degraded operations: Decrypt-only mode using cached keys when KMS is unavailable.
     * New encryptions are blocked to prevent data loss.
     */
    DEGRADED("Decrypt-only with cached keys"),
    
    /**
     * Locked operations: No operations allowed due to security concerns.
     * Used when cache is compromised or system integrity is in question.
     */
    LOCKED("No operations allowed");
    
    private final String description;
    
    FailureMode(String description) {
        this.description = description;
    }
    
    /**
     * Business logic: Determine if encryption operations are permitted.
     */
    public boolean canEncrypt() {
        return this == NORMAL;
    }
    
    /**
     * Business logic: Determine if decryption operations are permitted.
     */
    public boolean canDecrypt() {
        return this == NORMAL || this == DEGRADED;
    }
    
    /**
     * Business logic: Determine if this mode allows any operations.
     */
    public boolean isOperational() {
        return this != LOCKED;
    }
    
    public String getDescription() {
        return description;
    }
}
