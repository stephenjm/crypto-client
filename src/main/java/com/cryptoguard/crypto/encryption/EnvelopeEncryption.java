package com.cryptoguard.crypto.encryption;

import com.cryptoguard.crypto.kms.DataKey;
import com.cryptoguard.crypto.kms.KmsProvider;

public class EnvelopeEncryption {

    private final KmsProvider kmsProvider;

    public EnvelopeEncryption(KmsProvider kmsProvider) {
        this.kmsProvider = kmsProvider;
    }

    public DataKey generateAndReturnDescriptor(String keyId, int keySize) throws Exception {
        return kmsProvider.generateDataKey(keyId, keySize);
    }
}
