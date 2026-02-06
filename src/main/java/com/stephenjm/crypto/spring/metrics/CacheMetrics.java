package com.stephenjm.crypto.spring.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Business metrics wrapper for cache operations.
 * Tracks our unique cache performance indicators and health metrics.
 */
@Component
public class CacheMetrics {
    
    private final MeterRegistry meterRegistry;
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);
    private final AtomicLong evictions = new AtomicLong(0);
    private final AtomicLong expirations = new AtomicLong(0);
    
    public CacheMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        initializeMeters();
    }
    
    /**
     * Business metric initialization with our domain-specific naming.
     */
    private void initializeMeters() {
        meterRegistry.gauge("crypto.cache.hits", cacheHits);
        meterRegistry.gauge("crypto.cache.misses", cacheMisses);
        meterRegistry.gauge("crypto.cache.evictions", evictions);
        meterRegistry.gauge("crypto.cache.expirations", expirations);
    }
    
    /**
     * Business operation: Record a cache hit with tier information.
     */
    public void recordCacheHit(String keyId, String tier) {
        cacheHits.incrementAndGet();
        Counter.builder("crypto.cache.tier.hits")
            .tag("tier", tier)
            .tag("keyId", sanitizeKeyId(keyId))
            .register(meterRegistry)
            .increment();
    }
    
    /**
     * Business operation: Record a cache miss.
     */
    public void recordCacheMiss(String keyId) {
        cacheMisses.incrementAndGet();
    }
    
    /**
     * Business operation: Record a cache write operation.
     */
    public void recordCacheWrite(String keyId, String tier) {
        Counter.builder("crypto.cache.writes")
            .tag("tier", tier)
            .tag("keyId", sanitizeKeyId(keyId))
            .register(meterRegistry)
            .increment();
    }
    
    /**
     * Business operation: Record key eviction with reason tracking.
     */
    public void recordKeyEviction(String keyId, String reason) {
        evictions.incrementAndGet();
        Counter.builder("crypto.cache.evictions")
            .tag("reason", reason)
            .register(meterRegistry)
            .increment();
    }
    
    /**
     * Business operation: Record key expiration event.
     */
    public void recordKeyExpiration(String keyId) {
        expirations.incrementAndGet();
    }
    
    /**
     * Business operation: Record expired key rejection.
     */
    public void recordExpiredKeyRejection(String keyId) {
        Counter.builder("crypto.cache.expired.rejections")
            .tag("keyId", sanitizeKeyId(keyId))
            .register(meterRegistry)
            .increment();
    }
    
    /**
     * Business operation: Record cache tier usage pattern.
     */
    public void recordCacheTier(String keyId, String tierResult) {
        Counter.builder("crypto.cache.tier.access")
            .tag("result", tierResult)
            .register(meterRegistry)
            .increment();
    }
    
    /**
     * Business operation: Time a cache operation.
     */
    public <T> T timeOperation(String operationName, java.util.function.Supplier<T> operation) {
        Timer timer = Timer.builder("crypto.cache.operation.duration")
            .tag("operation", operationName)
            .register(meterRegistry);
        
        return timer.record(operation);
    }
    
    /**
     * Business logic: Sanitize key ID for metrics to avoid high cardinality.
     */
    private String sanitizeKeyId(String keyId) {
        // Keep only the prefix to avoid high cardinality in metrics
        if (keyId == null || keyId.length() < 8) {
            return "unknown";
        }
        return keyId.substring(0, 8) + "...";
    }
    
    /**
     * Business metric: Get current cache hit count.
     */
    public long getCacheHits() {
        return cacheHits.get();
    }
    
    /**
     * Business metric: Get current cache miss count.
     */
    public long getCacheMisses() {
        return cacheMisses.get();
    }
    
    /**
     * Business metric: Calculate hit ratio.
     */
    public double getHitRatio() {
        long hits = cacheHits.get();
        long misses = cacheMisses.get();
        long total = hits + misses;
        return total == 0 ? 0.0 : (double) hits / total * 100.0;
    }
}
