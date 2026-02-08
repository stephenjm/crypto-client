package com.crypto.security.exception;

/**
 * Exception thrown when signature verification fails.
 */
public class InvalidSignatureException extends KmsException {
    
    public InvalidSignatureException(String message) {
        super(message);
    }
    
    public InvalidSignatureException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidSignatureException(Throwable cause) {
        super(cause);
    }
}
