package com.crypto.security.encryption;

import com.crypto.security.kms.DataKey;
import com.crypto.security.kms.KmsProvider;

public class EnvelopeEncryption {

    private final KmsProvider kmsProvider;

    public EnvelopeEncryption(KmsProvider kmsProvider) {
        this.kmsProvider = kmsProvider;
    }

    public DataKey generateAndReturnDescriptor(String keyId, int keySize) throws Exception {
        return kmsProvider.generateDataKey(keyId, keySize);
    }
}
