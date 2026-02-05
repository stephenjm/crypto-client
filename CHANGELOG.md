# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.0] - 2026-02-05

### Added
- **Core Library**
  - `KmsClient` interface with builder pattern
  - `KmsClientBuilder` for creating client instances
  - Support for AutoCloseable (try-with-resources)
  
- **Providers**
  - `KmsProvider` interface for provider implementations
  - `MockKmsProvider` for local development and testing
    - Pre-configured master-key-1 and hmac-key-1
    - In-memory key storage
    - Full encryption, decryption, signing, and verification support
  - `AwsKmsProvider` stub for future AWS KMS integration
  - `GcpKmsProvider` stub for future GCP Cloud KMS integration
  - `AzureKeyVaultProvider` stub for future Azure Key Vault integration

- **Model Classes** (with builder pattern)
  - `EncryptRequest` and `EncryptResponse`
  - `DecryptRequest` and `DecryptResponse`
  - `GenerateDataKeyRequest` and `GenerateDataKeyResponse`
  - `SignRequest` and `SignResponse`
  - `VerifyRequest`
  - `EnvelopeEncryptionResult`
  - `KeySpec` enum (AES_256, AES_128)
  - `SigningAlgorithm` enum (HMAC_SHA_256, HMAC_SHA_384, HMAC_SHA_512)

- **Cryptography**
  - `AesGcmCipher` - AES-256-GCM authenticated encryption
    - Random IV generation for each encryption
    - Authenticated encryption (AEAD)
    - Support for both AES-256 and AES-128
  - `EnvelopeEncryption` - Efficient encryption for large data
    - Local data encryption with data keys
    - KMS-protected data key encryption
    - Memory safety (zeros out plaintext keys)

- **Exception Hierarchy**
  - `KmsException` - Base exception
  - `EncryptionException` - Encryption failures
  - `DecryptionException` - Decryption failures
  - `InvalidSignatureException` - Signature verification failures
  - `ProviderException` - Provider-specific errors

- **Testing**
  - Comprehensive unit tests (59 tests)
  - `AesGcmCipherTest` - Cipher operations
  - `MockKmsProviderTest` - Provider functionality
  - `KmsClientTest` - Client operations
  - `EnvelopeEncryptionTest` - Envelope encryption flow
  - `ModelTest` - Model classes and builders
  - `IntegrationTest` - End-to-end workflows
  - 80%+ code coverage

- **Build & Documentation**
  - Gradle build with Java 17 support
  - Gradle Wrapper for consistent builds
  - `.gitignore` for Gradle projects
  - Comprehensive README.md with examples
  - CHANGELOG.md

### Security
- Constant-time signature comparison to prevent timing attacks
- Memory safety: plaintext keys are zeroed after use
- Random IV generation for each encryption operation
- AES-256-GCM authenticated encryption with integrity checks

### Technical Details
- Java 17 minimum requirement
- No required runtime dependencies
- Optional cloud provider SDKs marked as optional in POM
- Builder pattern for all request/response objects
- Immutable model classes with proper equals/hashCode/toString

### Known Limitations
- AWS, GCP, and Azure providers are stubs (not implemented)
- No key rotation utilities yet
- No retry policy with exponential backoff yet
- MockKmsProvider stores keys in memory only (not persistent)

## [Unreleased]

### Planned for v0.2.0
- AWS KMS provider implementation
- Retry policy with exponential backoff
- Configuration class for timeouts and retries
- Key rotation helper utilities

### Planned for v0.3.0
- GCP Cloud KMS provider implementation
- Azure Key Vault provider implementation

### Future Considerations
- Performance benchmarks
- Metrics and observability integration
- Circuit breaker pattern for provider failures
- Connection pooling for cloud providers
- Async/reactive API support

---

[0.1.0]: https://github.com/stephenjm/crypto-client/releases/tag/v0.1.0
[Unreleased]: https://github.com/stephenjm/crypto-client/compare/v0.1.0...HEAD
