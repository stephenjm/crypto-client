package com.stephenjm.crypto.spring.events;

import com.stephenjm.crypto.spring.model.KeyRotationEvent;

/**
 * Domain event for cache invalidation notifications.
 * Represents our unique event when a cached key should be evicted.
 */
public record CacheInvalidationEvent(
    String keyId,
    String reason,
    java.time.Instant timestamp
) {
    public CacheInvalidationEvent(String keyId) {
        this(keyId, "manual-invalidation", java.time.Instant.now());
    }
    
    /**
     * Create from key rotation event - our unique conversion logic.
     */
    public static CacheInvalidationEvent fromKeyRotation(KeyRotationEvent event) {
        return new CacheInvalidationEvent(
            event.keyId(),
            "key-rotation:" + event.toState(),
            event.timestamp()
        );
    }
}
