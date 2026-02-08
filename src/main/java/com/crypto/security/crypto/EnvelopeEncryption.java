package com.crypto.security.crypto;

import com.crypto.security.kms.KmsClient;

import com.crypto.security.model.*;


import java.util.Arrays;

/**
 * Envelope encryption helper for encrypting data with data encryption keys (DEKs).
 * 
 * Envelope encryption flow:
 * 1. Generate DEK from KMS
 * 2. Encrypt data with DEK (local, fast)
 * 3. Store encrypted data + encrypted DEK
 * 4. Discard plaintext DEK
 * 
 * Decryption flow:
 * 1. Decrypt DEK with KMS
 * 2. Decrypt data with DEK (local, fast)
 * 3. Discard plaintext DEK
 */
public class EnvelopeEncryption {
    
    private final KmsClient kmsClient;
    private final AesGcmCipher cipher;
    
    public EnvelopeEncryption(KmsClient kmsClient) {
        this.kmsClient = kmsClient;
        this.cipher = new AesGcmCipher();
    }
    
    /**
     * Encrypt data using envelope encryption.
     * 
     * @param plaintext Data to encrypt
     * @param masterKeyId KMS master key ID
     * @return Encrypted data bundle (ciphertext + encrypted DEK)
     */
    public EnvelopeEncryptionResult encrypt(byte[] plaintext, String masterKeyId) {
        // Generate data key from KMS
        GenerateDataKeyResponse dataKey = kmsClient.generateDataKey(
            GenerateDataKeyRequest.builder()
                .keyId(masterKeyId)
                .keySpec(KeySpec.AES_256)
                .build()
        );
        
        try {
            // Encrypt data with plaintext DEK (local operation - fast!)
            byte[] ciphertext = cipher.encrypt(plaintext, dataKey.getPlaintextKey());
            
            return EnvelopeEncryptionResult.builder()
                .ciphertext(ciphertext)
                .encryptedDataKey(dataKey.getCiphertextKey())
                .masterKeyId(masterKeyId)
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Envelope encryption failed", e);
        } finally {
            // Zero out plaintext DEK from memory
            Arrays.fill(dataKey.getPlaintextKey(), (byte) 0);
        }
    }
    
    /**
     * Decrypt data using envelope encryption.
     * 
     * @param result Encrypted data bundle
     * @return Plaintext data
     */
    public byte[] decrypt(EnvelopeEncryptionResult result) {
        // Decrypt DEK with KMS
        DecryptResponse decryptedKey = kmsClient.decrypt(
            DecryptRequest.builder()
                .ciphertext(result.getEncryptedDataKey())
                .build()
        );
        
        try {
            // Decrypt data with plaintext DEK (local operation - fast!)
            return cipher.decrypt(result.getCiphertext(), decryptedKey.getPlaintext());
        } catch (Exception e) {
            throw new RuntimeException("Envelope decryption failed", e);
        } finally {
            // Zero out plaintext DEK from memory
            Arrays.fill(decryptedKey.getPlaintext(), (byte) 0);
        }
    }
}
