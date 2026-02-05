package com.stephenjm.crypto.provider;

import com.stephenjm.crypto.exception.ProviderException;
import com.stephenjm.crypto.model.*;

/**
 * GCP Cloud KMS provider adapter (stub implementation).
 * 
 * This is a placeholder for GCP Cloud KMS integration.
 * Full implementation would use GCP SDK to interact with Cloud KMS service.
 * Requires GCP credentials configured (service account, etc.)
 * 
 * To use this provider, include the GCP SDK dependency:
 * <pre>
 * &lt;dependency&gt;
 *   &lt;groupId&gt;com.google.cloud&lt;/groupId&gt;
 *   &lt;artifactId&gt;google-cloud-kms&lt;/artifactId&gt;
 *   &lt;version&gt;2.20.0&lt;/version&gt;
 * &lt;/dependency&gt;
 * </pre>
 */
public class GcpKmsProvider implements KmsProvider {
    
    public GcpKmsProvider() {
        throw new UnsupportedOperationException(
            "GCP Cloud KMS provider is not yet implemented. " +
            "This is a stub for future implementation. " +
            "Use MockKmsProvider for local development and testing."
        );
    }
    
    @Override
    public EncryptResponse encrypt(EncryptRequest request) {
        throw new ProviderException("GCP KMS provider not implemented");
    }
    
    @Override
    public DecryptResponse decrypt(DecryptRequest request) {
        throw new ProviderException("GCP KMS provider not implemented");
    }
    
    @Override
    public GenerateDataKeyResponse generateDataKey(GenerateDataKeyRequest request) {
        throw new ProviderException("GCP KMS provider not implemented");
    }
    
    @Override
    public SignResponse sign(SignRequest request) {
        throw new ProviderException("GCP KMS provider not implemented");
    }
    
    @Override
    public boolean verify(VerifyRequest request) {
        throw new ProviderException("GCP KMS provider not implemented");
    }
    
    @Override
    public String getProviderName() {
        return "GcpKmsProvider";
    }
    
    @Override
    public void close() {
        // No-op for stub
    }
}
