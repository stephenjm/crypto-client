package com.stephenjm.crypto.spring.failover;

import com.stephenjm.crypto.spring.model.FailureMode;

import java.time.Instant;

/**
 * Domain event for failure mode transitions.
 * Tracks our operational state changes for audit and monitoring.
 */
public record FailureModeTransitionEvent(
    FailureMode fromMode,
    FailureMode toMode,
    String reason,
    Instant timestamp
) {
    /**
     * Our business logic: Check if this transition represents a degradation.
     */
    public boolean isDegradation() {
        return (fromMode == FailureMode.NORMAL && toMode == FailureMode.DEGRADED) ||
               (fromMode == FailureMode.NORMAL && toMode == FailureMode.LOCKED) ||
               (fromMode == FailureMode.DEGRADED && toMode == FailureMode.LOCKED);
    }
    
    /**
     * Our business logic: Check if this transition represents a recovery.
     */
    public boolean isRecovery() {
        return (fromMode == FailureMode.DEGRADED && toMode == FailureMode.NORMAL) ||
               (fromMode == FailureMode.LOCKED && toMode == FailureMode.NORMAL) ||
               (fromMode == FailureMode.LOCKED && toMode == FailureMode.DEGRADED);
    }
    
    /**
     * Our business logic: Check if this is a critical security event.
     */
    public boolean isCriticalSecurityEvent() {
        return toMode == FailureMode.LOCKED || 
               reason.contains("COMPROMISED") ||
               reason.contains("SECURITY");
    }
}
