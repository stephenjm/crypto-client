package com.stephenjm.crypto;

import com.stephenjm.crypto.model.*;
import com.stephenjm.crypto.provider.KmsProvider;

import java.time.Duration;
import java.util.Objects;

/**
 * Builder for creating KmsClient instances.
 * 
 * Example usage:
 * <pre>
 * KmsClient client = KmsClient.builder()
 *     .provider(new MockKmsProvider("http://localhost:8080"))
 *     .timeout(Duration.ofSeconds(5))
 *     .build();
 * </pre>
 */
public class KmsClientBuilder {
    
    private KmsProvider provider;
    private Duration timeout;
    
    KmsClientBuilder() {
        this.timeout = Duration.ofSeconds(30); // Default timeout
    }
    
    /**
     * Set the KMS provider.
     * 
     * @param provider KMS provider implementation
     * @return this builder
     */
    public KmsClientBuilder provider(KmsProvider provider) {
        this.provider = provider;
        return this;
    }
    
    /**
     * Set the operation timeout.
     * 
     * @param timeout operation timeout
     * @return this builder
     */
    public KmsClientBuilder timeout(Duration timeout) {
        this.timeout = timeout;
        return this;
    }
    
    /**
     * Build the KmsClient instance.
     * 
     * @return KmsClient instance
     * @throws IllegalStateException if provider is not set
     */
    public KmsClient build() {
        Objects.requireNonNull(provider, "provider cannot be null");
        return new DefaultKmsClient(provider, timeout);
    }
    
    /**
     * Default implementation of KmsClient.
     */
    private static class DefaultKmsClient implements KmsClient {
        
        private final KmsProvider provider;
        private final Duration timeout;
        
        DefaultKmsClient(KmsProvider provider, Duration timeout) {
            this.provider = provider;
            this.timeout = timeout;
        }
        
        @Override
        public EncryptResponse encrypt(EncryptRequest request) {
            return provider.encrypt(request);
        }
        
        @Override
        public DecryptResponse decrypt(DecryptRequest request) {
            return provider.decrypt(request);
        }
        
        @Override
        public GenerateDataKeyResponse generateDataKey(GenerateDataKeyRequest request) {
            return provider.generateDataKey(request);
        }
        
        @Override
        public SignResponse sign(SignRequest request) {
            return provider.sign(request);
        }
        
        @Override
        public boolean verify(VerifyRequest request) {
            return provider.verify(request);
        }
        
        @Override
        public void close() {
            provider.close();
        }
    }
}
