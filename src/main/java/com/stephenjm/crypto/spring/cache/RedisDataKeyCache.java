package com.stephenjm.crypto.spring.cache;

import com.stephenjm.crypto.spring.config.CryptoClientProperties;
import com.stephenjm.crypto.spring.metrics.CacheMetrics;
import com.stephenjm.crypto.spring.model.CachedDataKey;
import com.stephenjm.crypto.spring.model.CacheStatistics;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * L2 cache wrapper using Redis for distributed key storage.
 * Implements our unique business logic for multi-instance cache coherency.
 * Redis/Lettuce handle the distributed caching while we add domain-specific operations.
 */
@Component
@ConditionalOnProperty(value = "crypto.cache.redis.enabled", havingValue = "true")
public class RedisDataKeyCache implements DataKeyCache {
    
    private final RedisTemplate<String, CachedDataKey> redisTemplate;
    private final RedisEncryptionManager encryptionManager;
    private final CacheMetrics metrics;
    private final CryptoClientProperties properties;
    
    // Our business metrics tracking
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong evictionCount = new AtomicLong(0);
    
    public RedisDataKeyCache(
            RedisTemplate<String, CachedDataKey> redisTemplate,
            RedisEncryptionManager encryptionManager,
            CacheMetrics metrics,
            CryptoClientProperties properties) {
        this.redisTemplate = redisTemplate;
        this.encryptionManager = encryptionManager;
        this.metrics = metrics;
        this.properties = properties;
    }
    
    @Override
    public Optional<CachedDataKey> retrieveDataKey(String keyId) {
        totalRequests.incrementAndGet();
        
        return metrics.timeOperation("redis-retrieve", () -> {
            try {
                CachedDataKey encrypted = redisTemplate.opsForValue().get(keyId);
                
                if (encrypted == null) {
                    metrics.recordCacheMiss(keyId);
                    return Optional.empty();
                }
                
                // Our unique decryption logic
                CachedDataKey decrypted = encryptionManager.decryptFromRedis(encrypted);
                
                // Our unique validation logic - check expiration
                if (decrypted.isExpired()) {
                    metrics.recordExpiredKeyRejection(keyId);
                    redisTemplate.delete(keyId); // Proactive cleanup
                    return Optional.empty();
                }
                
                metrics.recordCacheHit(keyId, "L2-REDIS");
                return Optional.of(decrypted);
            } catch (Exception e) {
                metrics.recordCacheMiss(keyId);
                return Optional.empty();
            }
        });
    }
    
    @Override
    public void cacheDataKey(String keyId, CachedDataKey dataKey) {
        // Our unique validation logic - don't cache already-expired keys
        if (dataKey.isExpired()) {
            metrics.recordExpiredKeyRejection(keyId);
            return;
        }
        
        metrics.timeOperation("redis-write", () -> {
            try {
                // Our unique encryption logic before Redis storage
                CachedDataKey encrypted = encryptionManager.encryptForRedis(dataKey);
                
                // Store with TTL from our business policy
                redisTemplate.opsForValue().set(
                    keyId,
                    encrypted,
                    properties.getCache().getRedis().getTtl()
                );
                
                metrics.recordCacheWrite(keyId, "L2-REDIS");
            } catch (Exception e) {
                // Log error but don't fail - Redis is optional
            }
            return null;
        });
    }
    
    @Override
    public void evictDataKey(String keyId) {
        redisTemplate.delete(keyId);
        evictionCount.incrementAndGet();
        metrics.recordKeyEviction(keyId, "explicit");
    }
    
    @Override
    public void evictExpiredKeys() {
        // Redis handles TTL-based expiration automatically
        // This is a no-op for our business logic
    }
    
    @Override
    public CacheStatistics getStatistics() {
        // Our unique statistics for L2 cache
        // Note: Redis doesn't provide hit/miss stats easily, so we track locally
        return new CacheStatistics(
            totalRequests.get(),
            metrics.getCacheHits(),
            metrics.getCacheMisses(),
            evictionCount.get(),
            0, // Redis handles expiration internally
            getApproximateSize(),
            -1 // Redis doesn't have a hard size limit we set
        );
    }
    
    @Override
    public void clearCache() {
        // Our unique clear operation - use SCAN to avoid blocking Redis
        redisTemplate.execute((org.springframework.data.redis.core.RedisCallback<Void>) connection -> {
            org.springframework.data.redis.core.Cursor<byte[]> cursor = connection.scan(
                org.springframework.data.redis.core.ScanOptions.scanOptions()
                    .match("*")
                    .count(100)
                    .build()
            );
            
            while (cursor.hasNext()) {
                connection.del(cursor.next());
            }
            cursor.close();
            return null;
        });
        metrics.recordKeyEviction("all", "clear-command");
    }
    
    /**
     * Our business operation: Estimate cache size.
     * Returns approximate count without blocking Redis.
     */
    private long getApproximateSize() {
        // Use DBSIZE for approximate count instead of keys()
        Long size = redisTemplate.execute((org.springframework.data.redis.core.RedisCallback<Long>) 
            connection -> connection.dbSize()
        );
        return size != null ? size : 0;
    }
}
