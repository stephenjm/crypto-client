package com.crypto.security.model;

/**
 * Key specification for symmetric encryption keys.
 */
public enum KeySpec {
    /**
     * 256-bit AES key (32 bytes)
     */
    AES_256(32),
    
    /**
     * 128-bit AES key (16 bytes)
     */
    AES_128(16);
    
    private final int keyLength;
    
    KeySpec(int keyLength) {
        this.keyLength = keyLength;
    }
    
    /**
     * Get the key length in bytes.
     * 
     * @return key length in bytes
     */
    public int getKeyLength() {
        return keyLength;
    }
}
