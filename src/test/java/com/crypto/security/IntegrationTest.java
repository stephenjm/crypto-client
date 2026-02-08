package com.crypto.security;

import com.crypto.security.crypto.EnvelopeEncryption;

import com.crypto.security.kms.KmsClient;
import com.crypto.security.model.*;

import com.crypto.security.provider.MockKmsProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the crypto-client library.
 */
class IntegrationTest {
    
    private KmsClient kmsClient;
    
    @BeforeEach
    void setUp() {
        kmsClient = KmsClient.builder()
            .provider(new MockKmsProvider("http://localhost:8080"))
            .build();
    }
    
    @AfterEach
    void tearDown() {
        if (kmsClient != null) {
            kmsClient.close();
        }
    }
    
    @Test
    void testCompleteEncryptionWorkflow() {
        // Given
        String sensitiveData = "Credit Card: 1234-5678-9012-3456";
        String masterKeyId = "master-key-1";
        
        // When - Encrypt
        EncryptResponse encryptResponse = kmsClient.encrypt(
            EncryptRequest.builder()
                .keyId(masterKeyId)
                .plaintext(sensitiveData.getBytes())
                .build()
        );
        
        // Then - Verify encryption
        assertNotNull(encryptResponse);
        assertEquals(masterKeyId, encryptResponse.getKeyId());
        assertNotNull(encryptResponse.getCiphertext());
        
        // When - Decrypt
        DecryptResponse decryptResponse = kmsClient.decrypt(
            DecryptRequest.builder()
                .ciphertext(encryptResponse.getCiphertext())
                .build()
        );
        
        // Then - Verify decryption
        assertNotNull(decryptResponse);
        assertEquals(sensitiveData, new String(decryptResponse.getPlaintext()));
    }
    
    @Test
    void testCompleteSigningWorkflow() {
        // Given
        String message = "Important message to sign";
        String hmacKeyId = "hmac-key-1";
        
        // When - Sign
        SignResponse signResponse = kmsClient.sign(
            SignRequest.builder()
                .keyId(hmacKeyId)
                .message(message.getBytes())
                .signingAlgorithm(SigningAlgorithm.HMAC_SHA_256)
                .build()
        );
        
        // Then - Verify signature creation
        assertNotNull(signResponse);
        assertEquals(hmacKeyId, signResponse.getKeyId());
        assertNotNull(signResponse.getSignature());
        
        // When - Verify signature
        boolean isValid = kmsClient.verify(
            VerifyRequest.builder()
                .keyId(hmacKeyId)
                .message(message.getBytes())
                .signature(signResponse.getSignature())
                .signingAlgorithm(SigningAlgorithm.HMAC_SHA_256)
                .build()
        );
        
        // Then - Verify validation
        assertTrue(isValid, "Signature should be valid");
    }
    
    @Test
    void testCompleteEnvelopeEncryptionWorkflow() {
        // Given
        String largeData = "This is a large amount of data that would be expensive to encrypt directly with KMS. " +
                          "Envelope encryption allows us to encrypt this data locally with a data encryption key (DEK), " +
                          "and only encrypt the DEK with KMS. This is much more efficient and cost-effective.";
        String masterKeyId = "master-key-1";
        
        EnvelopeEncryption envelopeEncryption = new EnvelopeEncryption(kmsClient);
        
        // When - Encrypt with envelope encryption
        EnvelopeEncryptionResult encrypted = envelopeEncryption.encrypt(
            largeData.getBytes(),
            masterKeyId
        );
        
        // Then - Verify encryption result
        assertNotNull(encrypted);
        assertNotNull(encrypted.getCiphertext());
        assertNotNull(encrypted.getEncryptedDataKey());
        assertEquals(masterKeyId, encrypted.getMasterKeyId());
        
        // When - Decrypt with envelope encryption
        byte[] decrypted = envelopeEncryption.decrypt(encrypted);
        
        // Then - Verify decryption
        assertNotNull(decrypted);
        assertEquals(largeData, new String(decrypted));
    }
    
    @Test
    void testDataKeyGenerationAndUsage() {
        // Given
        String masterKeyId = "master-key-1";
        
        // When - Generate data key
        GenerateDataKeyResponse dataKeyResponse = kmsClient.generateDataKey(
            GenerateDataKeyRequest.builder()
                .keyId(masterKeyId)
                .keySpec(KeySpec.AES_256)
                .build()
        );
        
        // Then - Verify data key generation
        assertNotNull(dataKeyResponse);
        assertEquals(masterKeyId, dataKeyResponse.getKeyId());
        assertEquals(32, dataKeyResponse.getPlaintextKey().length);
        assertNotNull(dataKeyResponse.getCiphertextKey());
        
        // When - Decrypt the encrypted data key
        DecryptResponse decryptResponse = kmsClient.decrypt(
            DecryptRequest.builder()
                .ciphertext(dataKeyResponse.getCiphertextKey())
                .build()
        );
        
        // Then - Verify decrypted data key matches plaintext key
        assertArrayEquals(dataKeyResponse.getPlaintextKey(), decryptResponse.getPlaintext());
    }
    
    @Test
    void testMultipleOperationsInSequence() {
        // Test that multiple operations work correctly in sequence
        
        // Operation 1: Encrypt data
        EncryptResponse encrypt1 = kmsClient.encrypt(
            EncryptRequest.builder()
                .keyId("master-key-1")
                .plaintext("Data 1".getBytes())
                .build()
        );
        
        // Operation 2: Sign message
        SignResponse sign1 = kmsClient.sign(
            SignRequest.builder()
                .keyId("hmac-key-1")
                .message("Message 1".getBytes())
                .signingAlgorithm(SigningAlgorithm.HMAC_SHA_256)
                .build()
        );
        
        // Operation 3: Encrypt more data
        EncryptResponse encrypt2 = kmsClient.encrypt(
            EncryptRequest.builder()
                .keyId("master-key-1")
                .plaintext("Data 2".getBytes())
                .build()
        );
        
        // Operation 4: Decrypt first data
        DecryptResponse decrypt1 = kmsClient.decrypt(
            DecryptRequest.builder()
                .ciphertext(encrypt1.getCiphertext())
                .build()
        );
        
        // Operation 5: Verify signature
        boolean isValid = kmsClient.verify(
            VerifyRequest.builder()
                .keyId("hmac-key-1")
                .message("Message 1".getBytes())
                .signature(sign1.getSignature())
                .signingAlgorithm(SigningAlgorithm.HMAC_SHA_256)
                .build()
        );
        
        // Operation 6: Decrypt second data
        DecryptResponse decrypt2 = kmsClient.decrypt(
            DecryptRequest.builder()
                .ciphertext(encrypt2.getCiphertext())
                .build()
        );
        
        // Verify all operations succeeded
        assertEquals("Data 1", new String(decrypt1.getPlaintext()));
        assertEquals("Data 2", new String(decrypt2.getPlaintext()));
        assertTrue(isValid);
    }
    
    @Test
    void testEncryptionWithDifferentKeysProducesDifferentResults() {
        // This test ensures that the same data encrypted with different operations
        // produces different ciphertexts (due to random IVs)
        
        String data = "Same data";
        
        EncryptResponse result1 = kmsClient.encrypt(
            EncryptRequest.builder()
                .keyId("master-key-1")
                .plaintext(data.getBytes())
                .build()
        );
        
        EncryptResponse result2 = kmsClient.encrypt(
            EncryptRequest.builder()
                .keyId("master-key-1")
                .plaintext(data.getBytes())
                .build()
        );
        
        // Ciphertexts should be different due to random IVs
        assertFalse(
            java.util.Arrays.equals(result1.getCiphertext(), result2.getCiphertext()),
            "Same data encrypted twice should produce different ciphertexts"
        );
        
        // But both should decrypt to the same plaintext
        DecryptResponse decrypt1 = kmsClient.decrypt(
            DecryptRequest.builder().ciphertext(result1.getCiphertext()).build()
        );
        
        DecryptResponse decrypt2 = kmsClient.decrypt(
            DecryptRequest.builder().ciphertext(result2.getCiphertext()).build()
        );
        
        assertArrayEquals(decrypt1.getPlaintext(), decrypt2.getPlaintext());
    }
}
