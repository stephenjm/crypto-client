package com.stephenjm.crypto.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for model classes.
 */
class ModelTest {
    
    @Test
    void testKeySpec_getKeyLength() {
        assertEquals(32, KeySpec.AES_256.getKeyLength());
        assertEquals(16, KeySpec.AES_128.getKeyLength());
    }
    
    @Test
    void testSigningAlgorithm_getAlgorithmName() {
        assertEquals("HmacSHA256", SigningAlgorithm.HMAC_SHA_256.getAlgorithmName());
        assertEquals("HmacSHA384", SigningAlgorithm.HMAC_SHA_384.getAlgorithmName());
        assertEquals("HmacSHA512", SigningAlgorithm.HMAC_SHA_512.getAlgorithmName());
    }
    
    @Test
    void testEncryptRequest_builder() {
        // Given
        String keyId = "test-key";
        byte[] plaintext = "test".getBytes();
        
        // When
        EncryptRequest request = EncryptRequest.builder()
            .keyId(keyId)
            .plaintext(plaintext)
            .build();
        
        // Then
        assertEquals(keyId, request.getKeyId());
        assertArrayEquals(plaintext, request.getPlaintext());
        assertNull(request.getEncryptionContext());
    }
    
    @Test
    void testEncryptRequest_builderWithNullKeyId_throwsException() {
        assertThrows(NullPointerException.class, () -> {
            EncryptRequest.builder()
                .plaintext("test".getBytes())
                .build();
        });
    }
    
    @Test
    void testEncryptResponse_builder() {
        // Given
        String keyId = "test-key";
        byte[] ciphertext = "encrypted".getBytes();
        String algorithm = "AES_256_GCM";
        
        // When
        EncryptResponse response = EncryptResponse.builder()
            .keyId(keyId)
            .ciphertext(ciphertext)
            .encryptionAlgorithm(algorithm)
            .build();
        
        // Then
        assertEquals(keyId, response.getKeyId());
        assertArrayEquals(ciphertext, response.getCiphertext());
        assertEquals(algorithm, response.getEncryptionAlgorithm());
    }
    
    @Test
    void testDecryptRequest_builder() {
        // Given
        byte[] ciphertext = "encrypted".getBytes();
        
        // When
        DecryptRequest request = DecryptRequest.builder()
            .ciphertext(ciphertext)
            .build();
        
        // Then
        assertArrayEquals(ciphertext, request.getCiphertext());
        assertNull(request.getEncryptionContext());
    }
    
    @Test
    void testDecryptResponse_builder() {
        // Given
        String keyId = "test-key";
        byte[] plaintext = "decrypted".getBytes();
        String algorithm = "AES_256_GCM";
        
        // When
        DecryptResponse response = DecryptResponse.builder()
            .keyId(keyId)
            .plaintext(plaintext)
            .encryptionAlgorithm(algorithm)
            .build();
        
        // Then
        assertEquals(keyId, response.getKeyId());
        assertArrayEquals(plaintext, response.getPlaintext());
        assertEquals(algorithm, response.getEncryptionAlgorithm());
    }
    
    @Test
    void testGenerateDataKeyRequest_builder() {
        // Given
        String keyId = "test-key";
        KeySpec keySpec = KeySpec.AES_256;
        
        // When
        GenerateDataKeyRequest request = GenerateDataKeyRequest.builder()
            .keyId(keyId)
            .keySpec(keySpec)
            .build();
        
        // Then
        assertEquals(keyId, request.getKeyId());
        assertEquals(keySpec, request.getKeySpec());
    }
    
    @Test
    void testGenerateDataKeyResponse_builder() {
        // Given
        String keyId = "test-key";
        byte[] plaintextKey = new byte[32];
        byte[] ciphertextKey = new byte[64];
        KeySpec keySpec = KeySpec.AES_256;
        
        // When
        GenerateDataKeyResponse response = GenerateDataKeyResponse.builder()
            .keyId(keyId)
            .plaintextKey(plaintextKey)
            .ciphertextKey(ciphertextKey)
            .keySpec(keySpec)
            .build();
        
        // Then
        assertEquals(keyId, response.getKeyId());
        assertArrayEquals(plaintextKey, response.getPlaintextKey());
        assertArrayEquals(ciphertextKey, response.getCiphertextKey());
        assertEquals(keySpec, response.getKeySpec());
    }
    
    @Test
    void testSignRequest_builder() {
        // Given
        String keyId = "test-key";
        byte[] message = "message".getBytes();
        SigningAlgorithm algorithm = SigningAlgorithm.HMAC_SHA_256;
        
        // When
        SignRequest request = SignRequest.builder()
            .keyId(keyId)
            .message(message)
            .signingAlgorithm(algorithm)
            .build();
        
        // Then
        assertEquals(keyId, request.getKeyId());
        assertArrayEquals(message, request.getMessage());
        assertEquals(algorithm, request.getSigningAlgorithm());
    }
    
    @Test
    void testSignRequest_builderWithDefaultAlgorithm() {
        // When
        SignRequest request = SignRequest.builder()
            .keyId("test-key")
            .message("message".getBytes())
            .build();
        
        // Then - should default to HMAC_SHA_256
        assertEquals(SigningAlgorithm.HMAC_SHA_256, request.getSigningAlgorithm());
    }
    
    @Test
    void testSignResponse_builder() {
        // Given
        String keyId = "test-key";
        byte[] signature = "signature".getBytes();
        SigningAlgorithm algorithm = SigningAlgorithm.HMAC_SHA_256;
        
        // When
        SignResponse response = SignResponse.builder()
            .keyId(keyId)
            .signature(signature)
            .signingAlgorithm(algorithm)
            .build();
        
        // Then
        assertEquals(keyId, response.getKeyId());
        assertArrayEquals(signature, response.getSignature());
        assertEquals(algorithm, response.getSigningAlgorithm());
    }
    
    @Test
    void testVerifyRequest_builder() {
        // Given
        String keyId = "test-key";
        byte[] message = "message".getBytes();
        byte[] signature = "signature".getBytes();
        SigningAlgorithm algorithm = SigningAlgorithm.HMAC_SHA_256;
        
        // When
        VerifyRequest request = VerifyRequest.builder()
            .keyId(keyId)
            .message(message)
            .signature(signature)
            .signingAlgorithm(algorithm)
            .build();
        
        // Then
        assertEquals(keyId, request.getKeyId());
        assertArrayEquals(message, request.getMessage());
        assertArrayEquals(signature, request.getSignature());
        assertEquals(algorithm, request.getSigningAlgorithm());
    }
    
    @Test
    void testEnvelopeEncryptionResult_builder() {
        // Given
        byte[] ciphertext = "encrypted".getBytes();
        byte[] encryptedDataKey = "key".getBytes();
        String masterKeyId = "master-key";
        
        // When
        EnvelopeEncryptionResult result = EnvelopeEncryptionResult.builder()
            .ciphertext(ciphertext)
            .encryptedDataKey(encryptedDataKey)
            .masterKeyId(masterKeyId)
            .build();
        
        // Then
        assertArrayEquals(ciphertext, result.getCiphertext());
        assertArrayEquals(encryptedDataKey, result.getEncryptedDataKey());
        assertEquals(masterKeyId, result.getMasterKeyId());
    }
    
    @Test
    void testEncryptRequest_equality() {
        // Given
        EncryptRequest request1 = EncryptRequest.builder()
            .keyId("key1")
            .plaintext("data".getBytes())
            .build();
        
        EncryptRequest request2 = EncryptRequest.builder()
            .keyId("key1")
            .plaintext("data".getBytes())
            .build();
        
        EncryptRequest request3 = EncryptRequest.builder()
            .keyId("key2")
            .plaintext("data".getBytes())
            .build();
        
        // Then
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
    }
    
    @Test
    void testEncryptRequest_toString() {
        // Given
        EncryptRequest request = EncryptRequest.builder()
            .keyId("test-key")
            .plaintext("test-data".getBytes())
            .build();
        
        // When
        String toString = request.toString();
        
        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("test-key"));
        assertTrue(toString.contains("plaintextLength"));
    }
}
