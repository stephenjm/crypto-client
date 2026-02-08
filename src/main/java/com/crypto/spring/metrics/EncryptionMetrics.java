package com.crypto.spring.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

/**
 * Metrics for encryption/decryption operations.
 * Tracks our unique performance and failure patterns.
 */
@Component
public class EncryptionMetrics {
    
    private final MeterRegistry meterRegistry;
    
    public EncryptionMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    /**
     * Our business metric: Record encryption operation.
     */
    public void recordEncryption(String keyId, boolean cached, long durationMs) {
        Counter.builder("crypto.encryption.operations")
            .tag("cached", String.valueOf(cached))
            .register(meterRegistry)
            .increment();
        
        Timer.builder("crypto.encryption.duration")
            .tag("cached", String.valueOf(cached))
            .register(meterRegistry)
            .record(java.time.Duration.ofMillis(durationMs));
    }
    
    /**
     * Our business metric: Record decryption operation.
     */
    public void recordDecryption(String keyId, long durationMs) {
        Counter.builder("crypto.decryption.operations")
            .register(meterRegistry)
            .increment();
        
        Timer.builder("crypto.decryption.duration")
            .register(meterRegistry)
            .record(java.time.Duration.ofMillis(durationMs));
    }
    
    /**
     * Our business metric: Record operation failure.
     */
    public void recordFailure(String operation, String reason) {
        Counter.builder("crypto.operation.failures")
            .tag("operation", operation)
            .tag("reason", reason)
            .register(meterRegistry)
            .increment();
    }
}
