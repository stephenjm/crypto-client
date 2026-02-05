package com.stephenjm.crypto.exception;

/**
 * Base exception for all KMS-related errors.
 */
public class KmsException extends RuntimeException {
    
    public KmsException(String message) {
        super(message);
    }
    
    public KmsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public KmsException(Throwable cause) {
        super(cause);
    }
}
