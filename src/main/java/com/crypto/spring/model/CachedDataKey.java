package com.crypto.spring.model;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Domain model representing a cached data encryption key with expiration tracking.
 * This wrapper adds business-specific metadata around encrypted key material.
 */
public record CachedDataKey(
    String keyId,
    byte[] encryptedKey,
    LocalDateTime cachedAt,
    LocalDateTime expiresAt,
    String keyVersion
) {
    /**
     * Business logic: Check if this cached key has expired based on our policy.
     * @return true if the key should no longer be used
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    /**
     * Business logic: Calculate remaining time-to-live for cache management.
     * @return remaining TTL in seconds, or 0 if expired
     */
    public long getRemainingTtlSeconds() {
        if (isExpired()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), expiresAt).getSeconds();
    }
    
    /**
     * Override equals to properly compare byte arrays
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CachedDataKey other)) return false;
        return keyId.equals(other.keyId) &&
               Arrays.equals(encryptedKey, other.encryptedKey) &&
               cachedAt.equals(other.cachedAt) &&
               expiresAt.equals(other.expiresAt) &&
               keyVersion.equals(other.keyVersion);
    }
    
    /**
     * Override hashCode to properly handle byte arrays
     */
    @Override
    public int hashCode() {
        int result = keyId.hashCode();
        result = 31 * result + Arrays.hashCode(encryptedKey);
        result = 31 * result + cachedAt.hashCode();
        result = 31 * result + expiresAt.hashCode();
        result = 31 * result + keyVersion.hashCode();
        return result;
    }
    
    /**
     * Override toString to avoid leaking key material in logs
     */
    @Override
    public String toString() {
        return "CachedDataKey[keyId=" + keyId + 
               ", keyVersion=" + keyVersion + 
               ", cachedAt=" + cachedAt + 
               ", expiresAt=" + expiresAt + 
               ", encryptedKeyLength=" + (encryptedKey != null ? encryptedKey.length : 0) + "]";
    }
}
