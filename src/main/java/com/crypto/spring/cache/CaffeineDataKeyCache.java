package com.crypto.spring.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.crypto.spring.config.CryptoClientProperties;
import com.crypto.spring.metrics.CacheMetrics;
import com.crypto.spring.model.CachedDataKey;
import com.crypto.spring.model.CacheStatistics;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * L1 cache wrapper using Caffeine library for in-memory key storage.
 * Implements our unique business logic for data key caching and lifecycle management.
 * Caffeine handles the low-level caching infrastructure while we add domain-specific operations.
 */
@Component
@Profile("!redis-only")
public class CaffeineDataKeyCache implements DataKeyCache {
    
    private final Cache<String, CachedDataKey> caffeineCache;
    private final CacheMetrics metrics;
    private final CryptoClientProperties properties;
    
    // Our business metrics tracking
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong evictionCount = new AtomicLong(0);
    private final AtomicLong expirationCount = new AtomicLong(0);
    
    public CaffeineDataKeyCache(CryptoClientProperties properties, CacheMetrics metrics) {
        this.properties = properties;
        this.metrics = metrics;
        
        // Caffeine library handles off-heap storage internally
        // We configure it with our business policies
        this.caffeineCache = Caffeine.newBuilder()
            .maximumSize(properties.getCache().getMaxSize())
            .expireAfterWrite(properties.getCache().getTtl())
            .removalListener(this::onKeyEviction) // Our unique listener
            .recordStats() // Enable stats for our metrics
            .build();
    }
    
    @Override
    public Optional<CachedDataKey> retrieveDataKey(String keyId) {
        totalRequests.incrementAndGet();
        
        return metrics.timeOperation("caffeine-retrieve", () -> {
            CachedDataKey cached = caffeineCache.getIfPresent(keyId);
            
            if (cached == null) {
                metrics.recordCacheMiss(keyId);
                return Optional.empty();
            }
            
            // Our unique validation logic - check expiration
            if (cached.isExpired()) {
                metrics.recordExpiredKeyRejection(keyId);
                caffeineCache.invalidate(keyId); // Proactive cleanup
                return Optional.empty();
            }
            
            metrics.recordCacheHit(keyId, "L1");
            return Optional.of(cached);
        });
    }
    
    @Override
    public void cacheDataKey(String keyId, CachedDataKey dataKey) {
        // Our unique validation logic - don't cache already-expired keys
        if (dataKey.isExpired()) {
            metrics.recordExpiredKeyRejection(keyId);
            return;
        }
        
        metrics.timeOperation("caffeine-write", () -> {
            caffeineCache.put(keyId, dataKey);
            metrics.recordCacheWrite(keyId, "L1");
            return null;
        });
    }
    
    @Override
    public void evictDataKey(String keyId) {
        caffeineCache.invalidate(keyId);
        metrics.recordKeyEviction(keyId, "explicit");
    }
    
    @Override
    public void evictExpiredKeys() {
        // Our unique cleanup strategy
        metrics.timeOperation("caffeine-cleanup", () -> {
            caffeineCache.asMap().forEach((keyId, dataKey) -> {
                if (dataKey.isExpired()) {
                    caffeineCache.invalidate(keyId);
                    metrics.recordKeyExpiration(keyId);
                }
            });
            return null;
        });
    }
    
    @Override
    public CacheStatistics getStatistics() {
        // Our unique statistics aggregation
        com.github.benmanes.caffeine.cache.stats.CacheStats stats = caffeineCache.stats();
        
        return new CacheStatistics(
            totalRequests.get(),
            stats.hitCount(),
            stats.missCount(),
            evictionCount.get(),
            expirationCount.get(),
            caffeineCache.estimatedSize(),
            properties.getCache().getMaxSize()
        );
    }
    
    @Override
    public void clearCache() {
        caffeineCache.invalidateAll();
        metrics.recordKeyEviction("all", "clear-command");
    }
    
    /**
     * Our unique eviction handler - called by Caffeine when keys are removed.
     * This is where we add our business logic around key lifecycle events.
     */
    private void onKeyEviction(String keyId, CachedDataKey key, RemovalCause cause) {
        evictionCount.incrementAndGet();
        
        // Our unique business logic based on eviction reason
        switch (cause) {
            case EXPIRED:
                expirationCount.incrementAndGet();
                metrics.recordKeyExpiration(keyId);
                break;
            case SIZE:
                metrics.recordKeyEviction(keyId, "size-limit");
                break;
            case EXPLICIT:
                metrics.recordKeyEviction(keyId, "explicit");
                break;
            case REPLACED:
                metrics.recordKeyEviction(keyId, "replaced");
                break;
            default:
                metrics.recordKeyEviction(keyId, cause.name().toLowerCase());
        }
    }
    
    /**
     * Our business operation: Get current cache size.
     */
    public long getCurrentSize() {
        return caffeineCache.estimatedSize();
    }
    
    /**
     * Our business operation: Check if cache is near capacity.
     */
    public boolean isNearCapacity() {
        long maxSize = properties.getCache().getMaxSize();
        long currentSize = caffeineCache.estimatedSize();
        return (double) currentSize / maxSize >= 0.9;
    }
}
