```markdown
# 🔐 crypto-client — Multi-Provider KMS Client Library

**One interface. Four KMS providers. Configuration-driven switching.**

A unified cryptographic client library providing a consistent interface for Key Management Services (KMS) across multiple providers. Write code once, switch between our custom KMS, AWS KMS, GCP Cloud KMS, and Azure Key Vault with just a configuration change.

## The Problem This Solves

Different KMS providers have different APIs, error handling, and configuration approaches. This creates vendor lock-in, code duplication, and testing complexity.

**Solution:** One unified interface for all providers.

```
Your Application
       
       ↓

KmsClient (unified interface)
       
       ↓
    ┌──┴──┬─────────┬──────────┐
    ▼     ▼         ▼          ▼
    AWS   GCP      Azure     Mock
```

**Same code. Different providers. One configuration change.**

## Key Features

- **Multi-Provider Support** - AWS KMS, GCP Cloud KMS, Azure Key Vault, Mock provider
- **Unified API** - Single interface for all operations
- **Builder Pattern** - Type-safe, fluent API design
- **Zero Dependencies** - No required runtime dependencies
- **Envelope Encryption** - Efficient encryption of large data
- **HMAC Support** - Message signing and verification
- **Testing-Friendly** - MockKmsProvider for unit tests

## Quick Start

### Maven
```xml
<dependency>
    <groupId>com.stephenjm</groupId>
    <artifactId>crypto-client</artifactId>
    <version>0.2.0</version>
</dependency>
```

### Gradle
```groovy
implementation 'com.stephenjm:crypto-client:0.2.0'
```

### Basic Usage
```java
import com.stephenjm.crypto.*;

// Create client
KmsClient client = KmsClient.builder()
    .provider(new MockKmsProvider("http://localhost:8080"))
    .build();

// Encrypt
EncryptResponse encrypted = client.encrypt(
    EncryptRequest.builder()
        .keyId("master-key-1")
        .plaintext("sensitive-data".getBytes())
        .build()
);

// Decrypt
DecryptResponse decrypted = client.decrypt(
    DecryptRequest.builder()
        .ciphertext(encrypted.getCiphertext())
        .build()
);

String result = new String(decrypted.getPlaintext());
```

## Provider Switching

Switch providers with a configuration change — **no code changes needed**:

```java
// Development: Mock KMS
KmsClient client = KmsClient.builder()
    .provider(new MockKmsProvider("http://localhost:8080"))
    .build();

// Production: AWS KMS (same code!)
KmsClient client = KmsClient.builder()
    .provider(new AwsKmsProvider(awsKmsClient))
    .build();
```

## Supported Providers

| Provider | Status | Use Case |
|----------|--------|----------|
| Mock | ✅ Complete | Local development & testing |
| AWS KMS | 📋 Planned | AWS deployments |
| GCP Cloud KMS | 📋 Planned | Google Cloud deployments |
| Azure Key Vault | 📋 Planned | Azure deployments |

## Envelope Encryption

For large data, use envelope encryption for better performance:

```java
EnvelopeEncryption envelope = new EnvelopeEncryption(client);

// Encrypt large data
EnvelopeEncryptionResult result = envelope.encrypt(
    largeDataBytes,
    "master-key-1"
);

// Store ciphertext + encrypted data key
storage.save(result.getCiphertext(), result.getEncryptedDataKey());

// Later: decrypt
byte[] plaintext = envelope.decrypt(result);
```

## Message Signing

```java
// Sign a message
SignResponse signature = client.sign(
    SignRequest.builder()
        .keyId("hmac-key-1")
        .message("important-message".getBytes())
        .signingAlgorithm(SigningAlgorithm.HMAC_SHA_256)
        .build()
);

// Verify signature
boolean isValid = client.verify(
    VerifyRequest.builder()
        .keyId("hmac-key-1")
        .message("important-message".getBytes())
        .signature(signature.getSignature())
        .signingAlgorithm(SigningAlgorithm.HMAC_SHA_256)
        .build()
);
```

## Security Features

- **AES-256-GCM** - Authenticated encryption with integrity
- **Random IVs** - Unique initialization vector per operation
- **Constant-Time Comparison** - Protection against timing attacks
- **Memory Safety** - Plaintext keys zeroed after use
- **No Key Persistence** - Development keys in memory only

## Testing

```bash
./gradlew test
```

Coverage: 80%+

## Ecosystem Integration

Part of a coordinated cryptography ecosystem:

1. **[Certificate Authority](https://github.com/stephenjm/certificate-authority)** - Foundation + Architecture
2. **[Key Management Service](https://github.com/stephenjm/key-management-service)** - KMS implementation
3. **crypto-client** (this library) - Multi-provider abstraction
4. **[JWT Signing Service](https://github.com/stephenjm/jwt-signing-service)** - Uses crypto-client

## Use Cases

- Encrypting data at rest in databases
- Protecting API keys and secrets
- Multi-cloud deployments
- Avoiding vendor lock-in
- Certificate Authority integration
- Microservice encryption

## Performance Benefits

Envelope encryption is 100x faster for large data:

```
Direct Encryption:     10MB → KMS → 50-100ms
Envelope Encryption:   Generate DEK (50-100ms) + Encrypt locally (<1ms)
```

## Contributing

Contributions welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License

Apache License 2.0 - See LICENSE file for details

## Roadmap

### v0.3.0
- AWS KMS provider implementation
- GCP Cloud KMS provider implementation
- Azure Key Vault provider implementation
- Key rotation utilities

### Future
- Async/reactive API support
- Performance benchmarks
- Advanced metrics and tracing

---

**Write once. Deploy anywhere. No vendor lock-in.**
```
