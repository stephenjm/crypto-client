package com.stephenjm.crypto.provider;

import com.stephenjm.crypto.model.*;

/**
 * KMS provider interface that all implementations must follow.
 */
public interface KmsProvider {
    
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
     * Get the provider name.
     * 
     * @return provider name
     */
    String getProviderName();
    
    /**
     * Close and cleanup provider resources.
     */
    void close();
}
