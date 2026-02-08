package com.crypto.spring.cache;

import com.crypto.spring.model.CachedDataKey;
import com.crypto.spring.model.CacheStatistics;

import java.util.Optional;

/**
 * Unified interface for data key caching strategies.
 * Abstracts the underlying cache implementation (Caffeine, Redis, etc.)
 * with our unique business operations for key lifecycle management.
 */
public interface DataKeyCache {
    
    /**
     * Business operation: Retrieve a cached data key by identifier.
     * Implements our key lookup logic with expiration checking.
     * 
     * @param keyId unique identifier for the cached key
     * @return cached key if present and not expired, empty otherwise
     */
    Optional<CachedDataKey> retrieveDataKey(String keyId);
    
    /**
     * Business operation: Store a data key in cache with our retention policy.
     * 
     * @param keyId unique identifier for the key
     * @param dataKey the key data to cache
     */
    void cacheDataKey(String keyId, CachedDataKey dataKey);
    
    /**
     * Business operation: Remove a specific key from cache.
     * Used for explicit invalidation during key rotation or compromise.
     * 
     * @param keyId the key to evict
     */
    void evictDataKey(String keyId);
    
    /**
     * Business operation: Clean up expired keys based on our TTL policy.
     * Implements our cache maintenance strategy.
     */
    void evictExpiredKeys();
    
    /**
     * Business metric: Get current cache performance statistics.
     * Provides visibility into our caching effectiveness.
     * 
     * @return current statistics snapshot
     */
    CacheStatistics getStatistics();
    
    /**
     * Business operation: Clear all cached keys.
     * Used for emergency security scenarios or system reset.
     */
    void clearCache();
}
