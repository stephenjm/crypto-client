package com.stephenjm.crypto;

import com.stephenjm.crypto.model.*;
import com.stephenjm.crypto.provider.MockKmsProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for KmsClient.
 */
class KmsClientTest {
    
    private KmsClient client;
    
    @BeforeEach
    void setUp() {
        client = KmsClient.builder()
            .provider(new MockKmsProvider("http://localhost:8080"))
            .timeout(Duration.ofSeconds(5))
            .build();
    }
    
    @AfterEach
    void tearDown() {
        if (client != null) {
            client.close();
        }
    }
    
    @Test
    void testBuilder_createsClient() {
        assertNotNull(client);
    }
    
    @Test
    void testBuilder_withoutProvider_throwsException() {
        assertThrows(NullPointerException.class, () -> {
            KmsClient.builder().build();
        });
    }
    
    @Test
    void testEncrypt() {
        // Given
        String plaintext = "Hello, World!";
        EncryptRequest request = EncryptRequest.builder()
            .keyId("master-key-1")
            .plaintext(plaintext.getBytes())
            .build();
        
        // When
        EncryptResponse response = client.encrypt(request);
        
        // Then
        assertNotNull(response);
        assertEquals("master-key-1", response.getKeyId());
        assertNotNull(response.getCiphertext());
    }
    
    @Test
    void testDecrypt() {
        // Given
        String plaintext = "Hello, World!";
        EncryptRequest encryptRequest = EncryptRequest.builder()
            .keyId("master-key-1")
            .plaintext(plaintext.getBytes())
            .build();
        
        EncryptResponse encryptResponse = client.encrypt(encryptRequest);
        
        DecryptRequest decryptRequest = DecryptRequest.builder()
            .ciphertext(encryptResponse.getCiphertext())
            .build();
        
        // When
        DecryptResponse decryptResponse = client.decrypt(decryptRequest);
        
        // Then
        assertNotNull(decryptResponse);
        assertArrayEquals(plaintext.getBytes(), decryptResponse.getPlaintext());
    }
    
    @Test
    void testGenerateDataKey() {
        // Given
        GenerateDataKeyRequest request = GenerateDataKeyRequest.builder()
            .keyId("master-key-1")
            .keySpec(KeySpec.AES_256)
            .build();
        
        // When
        GenerateDataKeyResponse response = client.generateDataKey(request);
        
        // Then
        assertNotNull(response);
        assertEquals("master-key-1", response.getKeyId());
        assertEquals(KeySpec.AES_256, response.getKeySpec());
        assertEquals(32, response.getPlaintextKey().length);
        assertNotNull(response.getCiphertextKey());
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
        SignResponse response = client.sign(request);
        
        // Then
        assertNotNull(response);
        assertEquals("hmac-key-1", response.getKeyId());
        assertNotNull(response.getSignature());
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
        
        SignResponse signResponse = client.sign(signRequest);
        
        VerifyRequest verifyRequest = VerifyRequest.builder()
            .keyId("hmac-key-1")
            .message(message.getBytes())
            .signature(signResponse.getSignature())
            .signingAlgorithm(SigningAlgorithm.HMAC_SHA_256)
            .build();
        
        // When
        boolean result = client.verify(verifyRequest);
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void testVerify_withInvalidSignature() {
        // Given
        String message = "Hello, World!";
        byte[] invalidSignature = "invalid".getBytes();
        
        VerifyRequest verifyRequest = VerifyRequest.builder()
            .keyId("hmac-key-1")
            .message(message.getBytes())
            .signature(invalidSignature)
            .signingAlgorithm(SigningAlgorithm.HMAC_SHA_256)
            .build();
        
        // When
        boolean result = client.verify(verifyRequest);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void testClose() {
        // Should not throw
        assertDoesNotThrow(() -> client.close());
    }
    
    @Test
    void testEndToEndEncryptionFlow() {
        // Given
        String originalText = "Sensitive data that needs encryption";
        
        // When - encrypt
        EncryptResponse encrypted = client.encrypt(
            EncryptRequest.builder()
                .keyId("master-key-1")
                .plaintext(originalText.getBytes())
                .build()
        );
        
        // When - decrypt
        DecryptResponse decrypted = client.decrypt(
            DecryptRequest.builder()
                .ciphertext(encrypted.getCiphertext())
                .build()
        );
        
        // Then
        String decryptedText = new String(decrypted.getPlaintext());
        assertEquals(originalText, decryptedText);
    }
}
