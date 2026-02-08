package com.crypto.spring.model;

import java.time.Instant;

/**
 * Domain event representing a key lifecycle state transition.
 * Used for cache invalidation and audit logging in our key management workflow.
 */
public record KeyRotationEvent(
    String keyId,
    String fromState,
    String toState,
    Instant timestamp,
    String reason
) {
    /**
     * Business logic: Determine if this event requires cache invalidation.
     * Keys transitioning to disabled or deleted states should be evicted.
     */
    public boolean requiresCacheInvalidation() {
        return "DIS".equals(toState) || "DEL".equals(toState) || "PURGED".equals(toState);
    }
    
    /**
     * Business logic: Determine if this event represents a critical security action.
     */
    public boolean isCriticalEvent() {
        return "DEL".equals(toState) || "PURGED".equals(toState) || "COMPROMISED".equals(reason);
    }
    
    /**
     * Business logic: Check if this is a routine rotation event.
     */
    public boolean isRoutineRotation() {
        return "ACT".equals(toState) && "SCHEDULED_ROTATION".equals(reason);
    }
}
