package com.stephenjm.crypto.exception;

/**
 * Exception thrown when decryption operations fail.
 */
public class DecryptionException extends KmsException {
    
    public DecryptionException(String message) {
        super(message);
    }
    
    public DecryptionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DecryptionException(Throwable cause) {
        super(cause);
    }
}
