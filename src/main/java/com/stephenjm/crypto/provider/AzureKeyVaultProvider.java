package com.stephenjm.crypto.provider;

import com.stephenjm.crypto.exception.ProviderException;
import com.stephenjm.crypto.model.*;

/**
 * Azure Key Vault provider adapter (stub implementation).
 * 
 * This is a placeholder for Azure Key Vault integration.
 * Full implementation would use Azure SDK to interact with Azure Key Vault service.
 * Requires Azure credentials configured (managed identity, etc.)
 * 
 * To use this provider, include the Azure SDK dependency:
 * <pre>
 * &lt;dependency&gt;
 *   &lt;groupId&gt;com.azure&lt;/groupId&gt;
 *   &lt;artifactId&gt;azure-security-keyvault-keys&lt;/artifactId&gt;
 *   &lt;version&gt;4.6.0&lt;/version&gt;
 * &lt;/dependency&gt;
 * </pre>
 */
public class AzureKeyVaultProvider implements KmsProvider {
    
    public AzureKeyVaultProvider() {
        throw new UnsupportedOperationException(
            "Azure Key Vault provider is not yet implemented. " +
            "This is a stub for future implementation. " +
            "Use MockKmsProvider for local development and testing."
        );
    }
    
    @Override
    public EncryptResponse encrypt(EncryptRequest request) {
        throw new ProviderException("Azure Key Vault provider not implemented");
    }
    
    @Override
    public DecryptResponse decrypt(DecryptRequest request) {
        throw new ProviderException("Azure Key Vault provider not implemented");
    }
    
    @Override
    public GenerateDataKeyResponse generateDataKey(GenerateDataKeyRequest request) {
        throw new ProviderException("Azure Key Vault provider not implemented");
    }
    
    @Override
    public SignResponse sign(SignRequest request) {
        throw new ProviderException("Azure Key Vault provider not implemented");
    }
    
    @Override
    public boolean verify(VerifyRequest request) {
        throw new ProviderException("Azure Key Vault provider not implemented");
    }
    
    @Override
    public String getProviderName() {
        return "AzureKeyVaultProvider";
    }
    
    @Override
    public void close() {
        // No-op for stub
    }
}
