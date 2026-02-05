package com.stephenjm.crypto.crypto;

import com.stephenjm.crypto.KmsClient;
import com.stephenjm.crypto.model.EnvelopeEncryptionResult;
import com.stephenjm.crypto.provider.MockKmsProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EnvelopeEncryption.
 */
class EnvelopeEncryptionTest {
    
    private KmsClient kmsClient;
    private EnvelopeEncryption envelopeEncryption;
    private SecureRandom secureRandom;
    
    @BeforeEach
    void setUp() {
        kmsClient = KmsClient.builder()
            .provider(new MockKmsProvider("http://localhost:8080"))
            .build();
        envelopeEncryption = new EnvelopeEncryption(kmsClient);
        secureRandom = new SecureRandom();
    }
    
    @AfterEach
    void tearDown() {
        if (kmsClient != null) {
            kmsClient.close();
        }
    }
    
    @Test
    void testEncryptDecrypt() {
        // Given
        String plaintext = "Hello, World! This is sensitive data.";
        String masterKeyId = "master-key-1";
        
        // When - encrypt
        EnvelopeEncryptionResult encrypted = envelopeEncryption.encrypt(
            plaintext.getBytes(), 
            masterKeyId
        );
        
        // Then - verify encrypted result
        assertNotNull(encrypted);
        assertNotNull(encrypted.getCiphertext());
        assertNotNull(encrypted.getEncryptedDataKey());
        assertEquals(masterKeyId, encrypted.getMasterKeyId());
        
        // When - decrypt
        byte[] decrypted = envelopeEncryption.decrypt(encrypted);
        
        // Then
        assertNotNull(decrypted);
        assertArrayEquals(plaintext.getBytes(), decrypted);
    }
    
    @Test
    void testEncryptDecrypt_withLargeData() {
        // Given
        byte[] plaintext = new byte[100000]; // 100KB
        secureRandom.nextBytes(plaintext);
        String masterKeyId = "master-key-1";
        
        // When
        EnvelopeEncryptionResult encrypted = envelopeEncryption.encrypt(plaintext, masterKeyId);
        byte[] decrypted = envelopeEncryption.decrypt(encrypted);
        
        // Then
        assertArrayEquals(plaintext, decrypted);
    }
    
    @Test
    void testEncryptDecrypt_withEmptyData() {
        // Given
        byte[] plaintext = new byte[0];
        String masterKeyId = "master-key-1";
        
        // When
        EnvelopeEncryptionResult encrypted = envelopeEncryption.encrypt(plaintext, masterKeyId);
        byte[] decrypted = envelopeEncryption.decrypt(encrypted);
        
        // Then
        assertArrayEquals(plaintext, decrypted);
    }
    
    @Test
    void testEncrypt_producesUniqueDataKeys() {
        // Given
        String plaintext = "Hello, World!";
        String masterKeyId = "master-key-1";
        
        // When
        EnvelopeEncryptionResult result1 = envelopeEncryption.encrypt(
            plaintext.getBytes(), 
            masterKeyId
        );
        EnvelopeEncryptionResult result2 = envelopeEncryption.encrypt(
            plaintext.getBytes(), 
            masterKeyId
        );
        
        // Then - each encryption should use a different data key
        assertFalse(
            java.util.Arrays.equals(result1.getCiphertext(), result2.getCiphertext()),
            "Each encryption should produce different ciphertext due to unique data keys"
        );
        assertFalse(
            java.util.Arrays.equals(result1.getEncryptedDataKey(), result2.getEncryptedDataKey()),
            "Each encryption should produce different encrypted data key"
        );
    }
    
    @Test
    void testEncryptDecrypt_multipleRounds() {
        // Given
        String originalText = "Sensitive data for multiple rounds";
        String masterKeyId = "master-key-1";
        
        // When - encrypt multiple times
        EnvelopeEncryptionResult round1 = envelopeEncryption.encrypt(
            originalText.getBytes(), 
            masterKeyId
        );
        byte[] decrypted1 = envelopeEncryption.decrypt(round1);
        
        EnvelopeEncryptionResult round2 = envelopeEncryption.encrypt(
            decrypted1, 
            masterKeyId
        );
        byte[] decrypted2 = envelopeEncryption.decrypt(round2);
        
        // Then
        assertArrayEquals(originalText.getBytes(), decrypted1);
        assertArrayEquals(originalText.getBytes(), decrypted2);
    }
    
    @Test
    void testDecrypt_withTamperedCiphertext_throwsException() {
        // Given
        String plaintext = "Hello, World!";
        String masterKeyId = "master-key-1";
        
        EnvelopeEncryptionResult encrypted = envelopeEncryption.encrypt(
            plaintext.getBytes(), 
            masterKeyId
        );
        
        // Tamper with ciphertext
        byte[] tamperedCiphertext = encrypted.getCiphertext().clone();
        tamperedCiphertext[tamperedCiphertext.length - 1] ^= 1;
        
        EnvelopeEncryptionResult tamperedResult = EnvelopeEncryptionResult.builder()
            .ciphertext(tamperedCiphertext)
            .encryptedDataKey(encrypted.getEncryptedDataKey())
            .masterKeyId(encrypted.getMasterKeyId())
            .build();
        
        // When / Then
        assertThrows(RuntimeException.class, () -> envelopeEncryption.decrypt(tamperedResult),
            "Decryption with tampered ciphertext should fail");
    }
    
    @Test
    void testDecrypt_withTamperedDataKey_throwsException() {
        // Given
        String plaintext = "Hello, World!";
        String masterKeyId = "master-key-1";
        
        EnvelopeEncryptionResult encrypted = envelopeEncryption.encrypt(
            plaintext.getBytes(), 
            masterKeyId
        );
        
        // Tamper with encrypted data key
        byte[] tamperedDataKey = encrypted.getEncryptedDataKey().clone();
        tamperedDataKey[tamperedDataKey.length - 1] ^= 1;
        
        EnvelopeEncryptionResult tamperedResult = EnvelopeEncryptionResult.builder()
            .ciphertext(encrypted.getCiphertext())
            .encryptedDataKey(tamperedDataKey)
            .masterKeyId(encrypted.getMasterKeyId())
            .build();
        
        // When / Then
        assertThrows(Exception.class, () -> envelopeEncryption.decrypt(tamperedResult),
            "Decryption with tampered data key should fail");
    }
}
