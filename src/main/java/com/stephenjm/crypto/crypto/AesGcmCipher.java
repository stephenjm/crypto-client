package com.stephenjm.crypto.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * AES-256-GCM cipher implementation.
 * 
 * GCM (Galois/Counter Mode) provides:
 * - Authenticated encryption (AEAD)
 * - Confidentiality + Integrity in one operation
 * - No padding needed
 */
public class AesGcmCipher {
    
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128; // bits
    private static final int GCM_IV_LENGTH = 12; // bytes (96 bits recommended)
    
    private final SecureRandom secureRandom;
    
    public AesGcmCipher() {
        this.secureRandom = new SecureRandom();
    }
    
    /**
     * Encrypt plaintext with AES-256-GCM.
     * 
     * @param plaintext Data to encrypt
     * @param key 32-byte (256-bit) or 16-byte (128-bit) encryption key
     * @return IV + ciphertext + authentication tag
     */
    public byte[] encrypt(byte[] plaintext, byte[] key) throws Exception {
        // Generate random IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);
        
        // Initialize cipher
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
        
        // Encrypt
        byte[] ciphertext = cipher.doFinal(plaintext);
        
        // Prepend IV to ciphertext (IV + ciphertext + tag)
        byte[] result = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(ciphertext, 0, result, iv.length, ciphertext.length);
        
        return result;
    }
    
    /**
     * Decrypt ciphertext with AES-256-GCM.
     * 
     * @param ciphertext IV + ciphertext + authentication tag
     * @param key 32-byte (256-bit) or 16-byte (128-bit) encryption key
     * @return Plaintext data
     */
    public byte[] decrypt(byte[] ciphertext, byte[] key) throws Exception {
        // Extract IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(ciphertext, 0, iv, 0, GCM_IV_LENGTH);
        
        // Extract ciphertext
        byte[] actualCiphertext = new byte[ciphertext.length - GCM_IV_LENGTH];
        System.arraycopy(ciphertext, GCM_IV_LENGTH, actualCiphertext, 0, actualCiphertext.length);
        
        // Initialize cipher
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
        
        // Decrypt and verify authentication tag
        return cipher.doFinal(actualCiphertext);
    }
}
