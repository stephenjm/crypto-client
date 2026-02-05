package com.stephenjm.crypto.exception;

/**
 * Exception thrown when encryption operations fail.
 */
public class EncryptionException extends KmsException {
    
    public EncryptionException(String message) {
        super(message);
    }
    
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public EncryptionException(Throwable cause) {
        super(cause);
    }
}
