package com.stephenjm.crypto.provider;

import com.stephenjm.crypto.exception.ProviderException;
import com.stephenjm.crypto.model.*;

/**
 * AWS KMS provider adapter (stub implementation).
 * 
 * This is a placeholder for AWS KMS integration.
 * Full implementation would use AWS SDK v2 to interact with AWS KMS service.
 * Requires AWS credentials configured (environment variables, IAM role, etc.)
 * 
 * To use this provider, include the AWS SDK dependency:
 * <pre>
 * &lt;dependency&gt;
 *   &lt;groupId&gt;software.amazon.awssdk&lt;/groupId&gt;
 *   &lt;artifactId&gt;kms&lt;/artifactId&gt;
 *   &lt;version&gt;2.20.0&lt;/version&gt;
 * &lt;/dependency&gt;
 * </pre>
 */
public class AwsKmsProvider implements KmsProvider {
    
    public AwsKmsProvider() {
        throw new UnsupportedOperationException(
            "AWS KMS provider is not yet implemented. " +
            "This is a stub for future implementation. " +
            "Use MockKmsProvider for local development and testing."
        );
    }
    
    @Override
    public EncryptResponse encrypt(EncryptRequest request) {
        throw new ProviderException("AWS KMS provider not implemented");
    }
    
    @Override
    public DecryptResponse decrypt(DecryptRequest request) {
        throw new ProviderException("AWS KMS provider not implemented");
    }
    
    @Override
    public GenerateDataKeyResponse generateDataKey(GenerateDataKeyRequest request) {
        throw new ProviderException("AWS KMS provider not implemented");
    }
    
    @Override
    public SignResponse sign(SignRequest request) {
        throw new ProviderException("AWS KMS provider not implemented");
    }
    
    @Override
    public boolean verify(VerifyRequest request) {
        throw new ProviderException("AWS KMS provider not implemented");
    }
    
    @Override
    public String getProviderName() {
        return "AwsKmsProvider";
    }
    
    @Override
    public void close() {
        // No-op for stub
    }
}
