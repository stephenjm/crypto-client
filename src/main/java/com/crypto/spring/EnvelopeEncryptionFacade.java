package com.crypto.spring;


import com.crypto.security.kms.KmsClient;
import com.crypto.security.crypto.AesGcmCipher;
import com.crypto.security.crypto.EnvelopeEncryption;
import com.crypto.security.model.*;
import com.crypto.spring.cache.DataKeyCache;
import com.crypto.spring.failover.FailureModeController;
import com.crypto.spring.model.CachedDataKey;
import com.crypto.spring.model.FailureMode;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

/**
 * Main facade for envelope encryption operations with intelligent caching.
 * Implements our unique business workflow: KMS integration + cache optimization + failure handling.
 * This is the primary entry point for Spring Boot applications using crypto-client.
 */
@Component
public class EnvelopeEncryptionFacade {
    
    private final KmsClient kmsClient;
    private final DataKeyCache dataKeyCache;
    private final FailureModeController failureModeController;
    private final EnvelopeEncryption envelopeEncryption;
    private final AesGcmCipher cipher;
    
    public EnvelopeEncryptionFacade(
            KmsClient kmsClient,
            DataKeyCache dataKeyCache,
            FailureModeController failureModeController) {
        this.kmsClient = kmsClient;
        this.dataKeyCache = dataKeyCache;
        this.failureModeController = failureModeController;
        this.envelopeEncryption = new EnvelopeEncryption(kmsClient);
        this.cipher = new AesGcmCipher();
    }
    
    /**
     * Our unique business operation: Encrypt data with intelligent data key caching.
     * Reduces KMS calls by reusing cached data keys when safe to do so.
     * 
     * @param plaintext data to encrypt
     * @param keyId KMS key identifier
     * @return encrypted envelope with cached or fresh data key
     */
    public EnvelopeEncryptionResult encryptWithCaching(byte[] plaintext, String keyId) {
        // Our unique failure mode check
        FailureMode currentMode = failureModeController.getCurrentMode();
        if (!currentMode.canEncrypt()) {
            throw new IllegalStateException(
                "Encryption not allowed in " + currentMode + " mode: " + currentMode.getDescription()
            );
        }
        
        // Our unique cache-first strategy
        Optional<CachedDataKey> cachedKey = dataKeyCache.retrieveDataKey(keyId);
        
        if (cachedKey.isPresent()) {
            // Use cached encrypted data key (no KMS call needed)
            return encryptWithCachedKey(plaintext, keyId, cachedKey.get());
        }
        
        // Cache miss - generate fresh data key from KMS and cache it
        return encryptWithFreshKey(plaintext, keyId);
    }
    
    /**
     * Our unique business operation: Decrypt data with cache-aware key retrieval.
     * 
     * @param encrypted envelope containing ciphertext and encrypted key
     * @return decrypted plaintext
     */
    public byte[] decryptWithCaching(EnvelopeEncryptionResult encrypted) {
        // Our unique failure mode check
        FailureMode currentMode = failureModeController.getCurrentMode();
        if (!currentMode.canDecrypt()) {
            throw new IllegalStateException(
                "Decryption not allowed in " + currentMode + " mode: " + currentMode.getDescription()
            );
        }
        
        // Delegate to existing envelope encryption logic
        return envelopeEncryption.decrypt(encrypted);
    }
    
    /**
     * Our unique workflow: Encrypt using a cached data key.
     */
    private EnvelopeEncryptionResult encryptWithCachedKey(byte[] plaintext, String keyId, CachedDataKey cachedKey) {
        // Decrypt the encrypted key using KMS (still faster than generating new key)
        byte[] plaintextKey = kmsClient.decrypt(
            DecryptRequest.builder()
                .ciphertext(cachedKey.encryptedKey())
                .build()
        ).getPlaintext();
        
        try {
            // Perform local encryption with the decrypted key - our unique workflow
            byte[] ciphertext = cipher.encrypt(plaintext, plaintextKey);
            
            return EnvelopeEncryptionResult.builder()
                .ciphertext(ciphertext)
                .encryptedDataKey(cachedKey.encryptedKey())
                .masterKeyId(keyId)
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Cached key encryption failed", e);
        } finally {
            // Zero out plaintext DEK from memory - our security practice
            Arrays.fill(plaintextKey, (byte) 0);
        }
    }
    
    /**
     * Our unique workflow: Generate fresh data key, cache it, and encrypt.
     */
    private EnvelopeEncryptionResult encryptWithFreshKey(byte[] plaintext, String keyId) {
        // Generate new data key from KMS
        GenerateDataKeyResponse dataKey = kmsClient.generateDataKey(
            GenerateDataKeyRequest.builder()
                .keyId(keyId)
                .keySpec(KeySpec.AES_256)
                .build()
        );
        
        // Cache the encrypted key for future use - our unique caching logic
        cacheDataKey(keyId, dataKey.getCiphertextKey());
        
        try {
            // Perform encryption
            byte[] ciphertext = cipher.encrypt(plaintext, dataKey.getPlaintextKey());
            
            return EnvelopeEncryptionResult.builder()
                .ciphertext(ciphertext)
                .encryptedDataKey(dataKey.getCiphertextKey())
                .masterKeyId(keyId)
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Fresh key encryption failed", e);
        } finally {
            // Zero out plaintext DEK from memory
            Arrays.fill(dataKey.getPlaintextKey(), (byte) 0);
        }
    }
    
    /**
     * Our unique caching logic: Store encrypted data key with business metadata.
     */
    private void cacheDataKey(String keyId, byte[] encryptedKey) {
        LocalDateTime now = LocalDateTime.now();
        CachedDataKey cachedKey = new CachedDataKey(
            keyId,
            encryptedKey,
            now,
            now.plusDays(7), // Our business policy: 7-day TTL
            "v1" // Our versioning scheme
        );
        
        dataKeyCache.cacheDataKey(keyId, cachedKey);
    }
    
    /**
     * Our business operation: Explicitly evict a cached key.
     * Used during key rotation or security incidents.
     */
    public void evictCachedKey(String keyId) {
        dataKeyCache.evictDataKey(keyId);
    }
    
    /**
     * Our business operation: Get current failure mode.
     */
    public FailureMode getCurrentFailureMode() {
        return failureModeController.getCurrentMode();
    }
}
