package com.crypto.security.provider;

import com.crypto.security.crypto.AesGcmCipher;
import com.crypto.security.exception.EncryptionException;
import com.crypto.security.exception.DecryptionException;

import com.crypto.security.model.*;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock KMS provider for local development and testing.
 * 
 * This implementation:
 * - Stores keys in memory (NOT for production!)
 * - Simulates KMS API calls locally
 * - No network latency
 * - Perfect for unit tests and local development
 */
public class MockKmsProvider implements KmsProvider {
    
    private final String endpoint;
    private final Map<String, byte[]> keys;
    private final SecureRandom secureRandom;
    private final AesGcmCipher cipher;
    
    public MockKmsProvider(String endpoint) {
        this.endpoint = endpoint;
        this.keys = new ConcurrentHashMap<>();
        this.secureRandom = new SecureRandom();
        this.cipher = new AesGcmCipher();
        
        // Pre-populate some keys for testing
        initializeDefaultKeys();
    }
    
    private void initializeDefaultKeys() {
        // Generate default master key
        byte[] masterKey = new byte[32]; // AES-256
        secureRandom.nextBytes(masterKey);
        keys.put("master-key-1", masterKey);
        
        // Generate default HMAC key
        byte[] hmacKey = new byte[32];
        secureRandom.nextBytes(hmacKey);
        keys.put("hmac-key-1", hmacKey);
    }
    
    @Override
    public EncryptResponse encrypt(EncryptRequest request) {
        byte[] masterKey = keys.get(request.getKeyId());
        if (masterKey == null) {
            throw new EncryptionException("Key not found: " + request.getKeyId());
        }
        
        try {
            byte[] ciphertext = cipher.encrypt(request.getPlaintext(), masterKey);
            
            return EncryptResponse.builder()
                .keyId(request.getKeyId())
                .ciphertext(ciphertext)
                .encryptionAlgorithm("AES_256_GCM")
                .build();
        } catch (Exception e) {
            throw new EncryptionException("Encryption failed", e);
        }
    }
    
    @Override
    public DecryptResponse decrypt(DecryptRequest request) {
        // Extract key ID from ciphertext metadata (simplified for mock)
        // In real implementation, this would be part of the ciphertext format
        
        // For now, try all keys (mock simplification)
        for (Map.Entry<String, byte[]> entry : keys.entrySet()) {
            try {
                byte[] plaintext = cipher.decrypt(request.getCiphertext(), entry.getValue());
                
                return DecryptResponse.builder()
                    .keyId(entry.getKey())
                    .plaintext(plaintext)
                    .encryptionAlgorithm("AES_256_GCM")
                    .build();
            } catch (Exception e) {
                // Try next key
                continue;
            }
        }
        
        throw new DecryptionException("Decryption failed - no valid key found");
    }
    
    @Override
    public GenerateDataKeyResponse generateDataKey(GenerateDataKeyRequest request) {
        byte[] masterKey = keys.get(request.getKeyId());
        if (masterKey == null) {
            throw new EncryptionException("Key not found: " + request.getKeyId());
        }
        
        // Generate data encryption key
        int keySize = request.getKeySpec().getKeyLength();
        byte[] plaintextDataKey = new byte[keySize];
        secureRandom.nextBytes(plaintextDataKey);
        
        // Encrypt data key with master key
        try {
            byte[] ciphertextDataKey = cipher.encrypt(plaintextDataKey, masterKey);
            
            return GenerateDataKeyResponse.builder()
                .keyId(request.getKeyId())
                .plaintextKey(plaintextDataKey)
                .ciphertextKey(ciphertextDataKey)
                .keySpec(request.getKeySpec())
                .build();
        } catch (Exception e) {
            throw new EncryptionException("Failed to generate data key", e);
        }
    }
    
    @Override
    public SignResponse sign(SignRequest request) {
        byte[] hmacKey = keys.get(request.getKeyId());
        if (hmacKey == null) {
            throw new EncryptionException("Key not found: " + request.getKeyId());
        }
        
        try {
            Mac mac = Mac.getInstance(request.getSigningAlgorithm().getAlgorithmName());
            SecretKeySpec keySpec = new SecretKeySpec(hmacKey, request.getSigningAlgorithm().getAlgorithmName());
            mac.init(keySpec);
            
            byte[] signature = mac.doFinal(request.getMessage());
            
            return SignResponse.builder()
                .keyId(request.getKeyId())
                .signature(signature)
                .signingAlgorithm(request.getSigningAlgorithm())
                .build();
        } catch (Exception e) {
            throw new EncryptionException("Signing failed", e);
        }
    }
    
    @Override
    public boolean verify(VerifyRequest request) {
        // Generate expected signature
        SignResponse expectedSignature = sign(
            SignRequest.builder()
                .keyId(request.getKeyId())
                .message(request.getMessage())
                .signingAlgorithm(request.getSigningAlgorithm())
                .build()
        );
        
        // Constant-time comparison
        return MessageDigest.isEqual(expectedSignature.getSignature(), request.getSignature());
    }
    
    @Override
    public String getProviderName() {
        return "MockKmsProvider";
    }
    
    @Override
    public void close() {
        // Clear keys from memory
        keys.clear();
    }
}
