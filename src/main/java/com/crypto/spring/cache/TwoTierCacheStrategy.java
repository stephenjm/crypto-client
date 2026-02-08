package com.crypto.spring.cache;

import com.crypto.spring.metrics.CacheMetrics;
import com.crypto.spring.model.CachedDataKey;
import com.crypto.spring.model.CacheStatistics;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Two-tier cache strategy implementing our unique cache promotion logic.
 * Orchestrates L1 (Caffeine) and optional L2 (Redis) with business-specific behaviors.
 * This is our wrapper that coordinates multiple cache layers.
 */
@Component
public class TwoTierCacheStrategy implements DataKeyCache {
    
    private final CaffeineDataKeyCache l1Cache;
    private final Optional<RedisDataKeyCache> l2Cache;
    private final CacheMetrics metrics;
    
    public TwoTierCacheStrategy(
            CaffeineDataKeyCache l1Cache,
            Optional<RedisDataKeyCache> l2Cache,
            CacheMetrics metrics) {
        this.l1Cache = l1Cache;
        this.l2Cache = l2Cache;
        this.metrics = metrics;
    }
    
    @Override
    public Optional<CachedDataKey> retrieveDataKey(String keyId) {
        // Our unique cache lookup logic with tier tracking
        
        // Step 1: Check L1 (fast local cache)
        Optional<CachedDataKey> l1Result = l1Cache.retrieveDataKey(keyId);
        if (l1Result.isPresent()) {
            metrics.recordCacheTier(keyId, "L1_HIT");
            return l1Result;
        }
        
        // Step 2: L1 miss - check if L2 is available
        if (l2Cache.isEmpty()) {
            metrics.recordCacheTier(keyId, "L1_MISS_NO_L2");
            return Optional.empty();
        }
        
        // Step 3: Check L2 (distributed cache)
        Optional<CachedDataKey> l2Result = l2Cache.get().retrieveDataKey(keyId);
        if (l2Result.isPresent()) {
            // Our unique promotion logic - populate L1 from L2
            CachedDataKey dataKey = l2Result.get();
            promoteToL1(keyId, dataKey);
            metrics.recordCacheTier(keyId, "L2_HIT_PROMOTED");
            return l2Result;
        }
        
        // Both tiers missed
        metrics.recordCacheTier(keyId, "L2_MISS");
        return Optional.empty();
    }
    
    @Override
    public void cacheDataKey(String keyId, CachedDataKey dataKey) {
        // Our unique write-through strategy: write to both tiers
        
        // Always write to L1 (local cache)
        l1Cache.cacheDataKey(keyId, dataKey);
        
        // Write to L2 if available (distributed cache)
        l2Cache.ifPresent(cache -> {
            try {
                cache.cacheDataKey(keyId, dataKey);
            } catch (Exception e) {
                // Our business logic: L2 failure doesn't fail the operation
                // L1 is sufficient for basic operation
            }
        });
    }
    
    @Override
    public void evictDataKey(String keyId) {
        // Our unique eviction strategy: evict from both tiers
        l1Cache.evictDataKey(keyId);
        l2Cache.ifPresent(cache -> cache.evictDataKey(keyId));
    }
    
    @Override
    public void evictExpiredKeys() {
        // Our unique cleanup strategy: clean both tiers
        l1Cache.evictExpiredKeys();
        l2Cache.ifPresent(DataKeyCache::evictExpiredKeys);
    }
    
    @Override
    public CacheStatistics getStatistics() {
        // Our unique statistics aggregation across tiers
        CacheStatistics l1Stats = l1Cache.getStatistics();
        
        if (l2Cache.isEmpty()) {
            return l1Stats;
        }
        
        // Combine L1 and L2 statistics with our business logic
        CacheStatistics l2Stats = l2Cache.get().getStatistics();
        return new CacheStatistics(
            l1Stats.totalRequests() + l2Stats.totalRequests(),
            l1Stats.cacheHits() + l2Stats.cacheHits(),
            l1Stats.cacheMisses() + l2Stats.cacheMisses(),
            l1Stats.evictions() + l2Stats.evictions(),
            l1Stats.expirations() + l2Stats.expirations(),
            l1Stats.currentSize(), // L1 size (most relevant)
            l1Stats.maxSize()
        );
    }
    
    @Override
    public void clearCache() {
        // Our unique clear strategy: clear both tiers
        l1Cache.clearCache();
        l2Cache.ifPresent(DataKeyCache::clearCache);
    }
    
    /**
     * Our unique promotion logic: Move L2 hit to L1 for faster subsequent access.
     * This implements our cache warming strategy.
     */
    private void promoteToL1(String keyId, CachedDataKey dataKey) {
        try {
            l1Cache.cacheDataKey(keyId, dataKey);
        } catch (Exception e) {
            // Our business logic: Promotion failure is non-critical
            // The key is still available from L2
        }
    }
    
    /**
     * Our business operation: Check if L2 is available.
     */
    public boolean isL2Available() {
        return l2Cache.isPresent();
    }
    
    /**
     * Our business operation: Get tier configuration.
     */
    public String getTierConfiguration() {
        return l2Cache.isPresent() ? "L1+L2" : "L1-only";
    }
}
