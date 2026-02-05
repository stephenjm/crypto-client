package com.stephenjm.crypto;

import com.stephenjm.crypto.model.*;

/**
 * Main KMS client interface providing encryption, decryption, and signing operations.
 * 
 * Supports multiple providers:
 * - MockKmsProvider (local development and testing)
 * - AwsKmsProvider (AWS KMS) - stub
 * - GcpKmsProvider (GCP Cloud KMS) - stub
 * - AzureKeyVaultProvider (Azure Key Vault) - stub
 * 
 * Example usage:
 * <pre>
 * KmsClient client = KmsClient.builder()
 *     .provider(new MockKmsProvider("http://localhost:8080"))
 *     .build();
 * 
 * EncryptResponse response = client.encrypt(
 *     EncryptRequest.builder()
 *         .keyId("master-key-1")
 *         .plaintext("sensitive-data".getBytes())
 *         .build()
 * );
 * </pre>
 */
public interface KmsClient extends AutoCloseable {
    
    /**
     * Encrypt plaintext data with specified key.
     * 
     * @param request encrypt request
     * @return encrypt response
     */
    EncryptResponse encrypt(EncryptRequest request);
    
    /**
     * Decrypt ciphertext data.
     * 
     * @param request decrypt request
     * @return decrypt response
     */
    DecryptResponse decrypt(DecryptRequest request);
    
    /**
     * Generate data encryption key for envelope encryption.
     * 
     * @param request generate data key request
     * @return generate data key response
     */
    GenerateDataKeyResponse generateDataKey(GenerateDataKeyRequest request);
    
    /**
     * Sign message with HMAC.
     * 
     * @param request sign request
     * @return sign response
     */
    SignResponse sign(SignRequest request);
    
    /**
     * Verify HMAC signature.
     * 
     * @param request verify request
     * @return true if signature is valid, false otherwise
     */
    boolean verify(VerifyRequest request);
    
    /**
     * Builder for creating KmsClient instances.
     * 
     * @return new KmsClientBuilder
     */
    static KmsClientBuilder builder() {
        return new KmsClientBuilder();
    }
    
    /**
     * Close the KMS client and release resources.
     */
    @Override
    void close();
}
