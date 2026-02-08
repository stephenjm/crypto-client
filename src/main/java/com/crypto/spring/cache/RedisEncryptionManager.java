package com.crypto.spring.cache;

import com.crypto.security.crypto.AesGcmCipher;
import com.crypto.spring.config.CryptoClientProperties;
import com.crypto.spring.model.CachedDataKey;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Business wrapper for encrypting cached keys before Redis storage.
 * Implements our unique security policy: encrypt data keys before distributed caching.
 * This ensures keys are protected even if Redis is compromised.
 */
@Component
public class RedisEncryptionManager {
    
    private final AesGcmCipher cipher;
    private final byte[] redisEncryptionKey;
    private final boolean encryptionEnabled;
    
    public RedisEncryptionManager(CryptoClientProperties properties) {
        this.cipher = new AesGcmCipher();
        this.encryptionEnabled = properties.getCache().getRedis().isEncryptionEnabled();
        
        // In production, this would come from a secure key management system
        // For our implementation, we generate it on startup (unique to this instance)
        this.redisEncryptionKey = generateRedisEncryptionKey();
    }
    
    /**
     * Our business logic: Wrap and encrypt a data key for Redis storage.
     * Adds an extra layer of protection for distributed cache.
     */
    public CachedDataKey encryptForRedis(CachedDataKey dataKey) {
        if (!encryptionEnabled) {
            return dataKey; // Pass-through if encryption disabled
        }
        
        try {
            // Our unique encryption workflow for Redis
            byte[] encryptedPayload = cipher.encrypt(
                dataKey.encryptedKey(),
                redisEncryptionKey
            );
            
            // Return wrapped version with double encryption
            return new CachedDataKey(
                dataKey.keyId(),
                encryptedPayload,
                dataKey.cachedAt(),
                dataKey.expiresAt(),
                dataKey.keyVersion()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt data key for Redis storage", e);
        }
    }
    
    /**
     * Our business logic: Unwrap and decrypt a data key from Redis storage.
     */
    public CachedDataKey decryptFromRedis(CachedDataKey encrypted) {
        if (!encryptionEnabled) {
            return encrypted; // Pass-through if encryption disabled
        }
        
        try {
            // Our unique decryption workflow for Redis
            byte[] decryptedPayload = cipher.decrypt(
                encrypted.encryptedKey(),
                redisEncryptionKey
            );
            
            // Return unwrapped version
            return new CachedDataKey(
                encrypted.keyId(),
                decryptedPayload,
                encrypted.cachedAt(),
                encrypted.expiresAt(),
                encrypted.keyVersion()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt data key from Redis storage", e);
        }
    }
    
    /**
     * Our unique key generation for Redis encryption.
     * In production, this would be managed externally.
     */
    private byte[] generateRedisEncryptionKey() {
        byte[] key = new byte[32]; // 256-bit key
        new SecureRandom().nextBytes(key);
        return key;
    }
    
    /**
     * Our business operation: Check if encryption is enabled.
     */
    public boolean isEncryptionEnabled() {
        return encryptionEnabled;
    }
}
