package com.crypto.security.provider;

import com.crypto.security.exception.DecryptionException;
import com.crypto.security.exception.EncryptionException;

import com.crypto.security.model.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MockKmsProvider.
 */
class MockKmsProviderTest {
    
    private MockKmsProvider provider;
    
    @BeforeEach
    void setUp() {
        provider = new MockKmsProvider("http://localhost:8080");
    }
    
    @AfterEach
    void tearDown() {
        provider.close();
    }
    
    @Test
    void testGetProviderName() {
        assertEquals("MockKmsProvider", provider.getProviderName());
    }
    
    @Test
    void testEncryptDecrypt() {
        // Given
        String plaintext = "Hello, World!";
        EncryptRequest encryptRequest = EncryptRequest.builder()
            .keyId("master-key-1")
            .plaintext(plaintext.getBytes())
            .build();
        
        // When
        EncryptResponse encryptResponse = provider.encrypt(encryptRequest);
        
        DecryptRequest decryptRequest = DecryptRequest.builder()
            .ciphertext(encryptResponse.getCiphertext())
            .build();
        
        DecryptResponse decryptResponse = provider.decrypt(decryptRequest);
        
        // Then
        assertNotNull(encryptResponse);
        assertEquals("master-key-1", encryptResponse.getKeyId());
        assertNotNull(encryptResponse.getCiphertext());
        
        assertNotNull(decryptResponse);
        assertEquals("master-key-1", decryptResponse.getKeyId());
        assertArrayEquals(plaintext.getBytes(), decryptResponse.getPlaintext());
    }
    
    @Test
    void testEncrypt_withUnknownKey_throwsException() {
        // Given
        EncryptRequest request = EncryptRequest.builder()
            .keyId("unknown-key")
            .plaintext("Hello".getBytes())
            .build();
        
        // When / Then
        assertThrows(EncryptionException.class, () -> provider.encrypt(request),
            "Should throw EncryptionException for unknown key");
    }
    
    @Test
    void testDecrypt_withInvalidCiphertext_throwsException() {
        // Given
        DecryptRequest request = DecryptRequest.builder()
            .ciphertext("invalid-ciphertext".getBytes())
            .build();
        
        // When / Then
        assertThrows(DecryptionException.class, () -> provider.decrypt(request),
            "Should throw DecryptionException for invalid ciphertext");
    }
    
    @Test
    void testGenerateDataKey_AES256() {
        // Given
        GenerateDataKeyRequest request = GenerateDataKeyRequest.builder()
            .keyId("master-key-1")
            .keySpec(KeySpec.AES_256)
            .build();
        
        // When
        GenerateDataKeyResponse response = provider.generateDataKey(request);
        
        // Then
        assertNotNull(response);
        assertEquals("master-key-1", response.getKeyId());
        assertEquals(KeySpec.AES_256, response.getKeySpec());
        assertEquals(32, response.getPlaintextKey().length);
        assertNotNull(response.getCiphertextKey());
    }
    
    @Test
    void testGenerateDataKey_AES128() {
        // Given
        GenerateDataKeyRequest request = GenerateDataKeyRequest.builder()
            .keyId("master-key-1")
            .keySpec(KeySpec.AES_128)
            .build();
        
        // When
        GenerateDataKeyResponse response = provider.generateDataKey(request);
        
        // Then
        assertNotNull(response);
        assertEquals(KeySpec.AES_128, response.getKeySpec());
        assertEquals(16, response.getPlaintextKey().length);
    }
    
    @Test
    void testGenerateDataKey_canDecryptDataKey() {
        // Given
        GenerateDataKeyRequest generateRequest = GenerateDataKeyRequest.builder()
            .keyId("master-key-1")
            .keySpec(KeySpec.AES_256)
            .build();
        
        GenerateDataKeyResponse generateResponse = provider.generateDataKey(generateRequest);
        
        // When - decrypt the encrypted data key
        DecryptRequest decryptRequest = DecryptRequest.builder()
            .ciphertext(generateResponse.getCiphertextKey())
            .build();
        
        DecryptResponse decryptResponse = provider.decrypt(decryptRequest);
        
        // Then
        assertArrayEquals(generateResponse.getPlaintextKey(), decryptResponse.getPlaintext(),
            "Decrypted data key should match plaintext data key");
    }
    
    @Test
    void testSign() {
        // Given
        String message = "Hello, World!";
        SignRequest request = SignRequest.builder()
            .keyId("hmac-key-1")
            .message(message.getBytes())
            .signingAlgorithm(SigningAlgorithm.HMAC_SHA_256)
            .build();
        
        // When
        SignResponse response = provider.sign(request);
        
        // Then
        assertNotNull(response);
        assertEquals("hmac-key-1", response.getKeyId());
        assertNotNull(response.getSignature());
        assertEquals(SigningAlgorithm.HMAC_SHA_256, response.getSigningAlgorithm());
    }
    
    @Test
    void testVerify_withValidSignature() {
        // Given
        String message = "Hello, World!";
        SignRequest signRequest = SignRequest.builder()
            .keyId("hmac-key-1")
            .message(message.getBytes())
            .signingAlgorithm(SigningAlgorithm.HMAC_SHA_256)
            .build();
        
        SignResponse signResponse = provider.sign(signRequest);
        
        VerifyRequest verifyRequest = VerifyRequest.builder()
            .keyId("hmac-key-1")
            .message(message.getBytes())
            .signature(signResponse.getSignature())
            .signingAlgorithm(SigningAlgorithm.HMAC_SHA_256)
            .build();
        
        // When
        boolean result = provider.verify(verifyRequest);
        
        // Then
        assertTrue(result, "Signature should be valid");
    }
    
    @Test
    void testVerify_withInvalidSignature() {
        // Given
        String message = "Hello, World!";
        byte[] invalidSignature = "invalid-signature".getBytes();
        
        VerifyRequest verifyRequest = VerifyRequest.builder()
            .keyId("hmac-key-1")
            .message(message.getBytes())
            .signature(invalidSignature)
            .signingAlgorithm(SigningAlgorithm.HMAC_SHA_256)
            .build();
        
        // When
        boolean result = provider.verify(verifyRequest);
        
        // Then
        assertFalse(result, "Invalid signature should not verify");
    }
    
    @Test
    void testVerify_withTamperedMessage() {
        // Given
        String message = "Hello, World!";
        SignRequest signRequest = SignRequest.builder()
            .keyId("hmac-key-1")
            .message(message.getBytes())
            .signingAlgorithm(SigningAlgorithm.HMAC_SHA_256)
            .build();
        
        SignResponse signResponse = provider.sign(signRequest);
        
        // Tamper with message
        String tamperedMessage = "Hello, World!!";
        VerifyRequest verifyRequest = VerifyRequest.builder()
            .keyId("hmac-key-1")
            .message(tamperedMessage.getBytes())
            .signature(signResponse.getSignature())
            .signingAlgorithm(SigningAlgorithm.HMAC_SHA_256)
            .build();
        
        // When
        boolean result = provider.verify(verifyRequest);
        
        // Then
        assertFalse(result, "Tampered message should not verify");
    }
    
    @Test
    void testClose_clearsKeys() {
        // Given
        EncryptRequest request = EncryptRequest.builder()
            .keyId("master-key-1")
            .plaintext("Hello".getBytes())
            .build();
        
        // Verify key exists before close
        assertDoesNotThrow(() -> provider.encrypt(request));
        
        // When
        provider.close();
        
        // Then - after close, keys should be cleared
        // Note: This test just verifies close doesn't throw
        assertDoesNotThrow(() -> provider.close());
    }
}
