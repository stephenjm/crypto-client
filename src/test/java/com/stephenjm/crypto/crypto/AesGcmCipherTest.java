package com.stephenjm.crypto.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AesGcmCipher.
 */
class AesGcmCipherTest {
    
    private AesGcmCipher cipher;
    private SecureRandom secureRandom;
    
    @BeforeEach
    void setUp() {
        cipher = new AesGcmCipher();
        secureRandom = new SecureRandom();
    }
    
    @Test
    void testEncryptDecrypt_withAES256Key() throws Exception {
        // Given
        byte[] plaintext = "Hello, World!".getBytes();
        byte[] key = new byte[32]; // AES-256
        secureRandom.nextBytes(key);
        
        // When
        byte[] ciphertext = cipher.encrypt(plaintext, key);
        byte[] decrypted = cipher.decrypt(ciphertext, key);
        
        // Then
        assertNotNull(ciphertext);
        assertNotNull(decrypted);
        assertArrayEquals(plaintext, decrypted);
        assertNotEquals(ciphertext.length, plaintext.length); // IV + ciphertext + tag
    }
    
    @Test
    void testEncryptDecrypt_withAES128Key() throws Exception {
        // Given
        byte[] plaintext = "Hello, World!".getBytes();
        byte[] key = new byte[16]; // AES-128
        secureRandom.nextBytes(key);
        
        // When
        byte[] ciphertext = cipher.encrypt(plaintext, key);
        byte[] decrypted = cipher.decrypt(ciphertext, key);
        
        // Then
        assertNotNull(ciphertext);
        assertNotNull(decrypted);
        assertArrayEquals(plaintext, decrypted);
    }
    
    @Test
    void testEncryptDecrypt_withLargeData() throws Exception {
        // Given
        byte[] plaintext = new byte[10000]; // 10KB
        secureRandom.nextBytes(plaintext);
        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        
        // When
        byte[] ciphertext = cipher.encrypt(plaintext, key);
        byte[] decrypted = cipher.decrypt(ciphertext, key);
        
        // Then
        assertArrayEquals(plaintext, decrypted);
    }
    
    @Test
    void testEncryptDecrypt_withEmptyData() throws Exception {
        // Given
        byte[] plaintext = new byte[0];
        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        
        // When
        byte[] ciphertext = cipher.encrypt(plaintext, key);
        byte[] decrypted = cipher.decrypt(ciphertext, key);
        
        // Then
        assertArrayEquals(plaintext, decrypted);
    }
    
    @Test
    void testEncrypt_producesUniqueIV() throws Exception {
        // Given
        byte[] plaintext = "Hello, World!".getBytes();
        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        
        // When
        byte[] ciphertext1 = cipher.encrypt(plaintext, key);
        byte[] ciphertext2 = cipher.encrypt(plaintext, key);
        
        // Then
        assertFalse(java.util.Arrays.equals(ciphertext1, ciphertext2),
            "Each encryption should produce different ciphertext due to unique IV");
    }
    
    @Test
    void testDecrypt_withWrongKey_throwsException() throws Exception {
        // Given
        byte[] plaintext = "Hello, World!".getBytes();
        byte[] key1 = new byte[32];
        byte[] key2 = new byte[32];
        secureRandom.nextBytes(key1);
        secureRandom.nextBytes(key2);
        
        byte[] ciphertext = cipher.encrypt(plaintext, key1);
        
        // When / Then
        assertThrows(Exception.class, () -> cipher.decrypt(ciphertext, key2),
            "Decryption with wrong key should fail");
    }
    
    @Test
    void testDecrypt_withTamperedCiphertext_throwsException() throws Exception {
        // Given
        byte[] plaintext = "Hello, World!".getBytes();
        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        
        byte[] ciphertext = cipher.encrypt(plaintext, key);
        
        // Tamper with ciphertext
        ciphertext[ciphertext.length - 1] ^= 1;
        
        // When / Then
        assertThrows(Exception.class, () -> cipher.decrypt(ciphertext, key),
            "Decryption with tampered ciphertext should fail authentication");
    }
    
    @Test
    void testCiphertextFormat() throws Exception {
        // Given
        byte[] plaintext = "Hello, World!".getBytes();
        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        
        // When
        byte[] ciphertext = cipher.encrypt(plaintext, key);
        
        // Then
        // IV (12 bytes) + ciphertext (same length as plaintext) + tag (16 bytes)
        int expectedMinLength = 12 + plaintext.length + 16;
        assertTrue(ciphertext.length >= expectedMinLength,
            "Ciphertext should contain IV + encrypted data + authentication tag");
    }
}
