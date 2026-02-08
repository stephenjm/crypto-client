package com.crypto.security.exception;

/**
 * Exception thrown when provider-specific errors occur.
 */
public class ProviderException extends KmsException {
    
    public ProviderException(String message) {
        super(message);
    }
    
    public ProviderException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ProviderException(Throwable cause) {
        super(cause);
    }
}
