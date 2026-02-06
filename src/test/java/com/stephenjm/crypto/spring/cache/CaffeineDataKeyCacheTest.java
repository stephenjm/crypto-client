package com.stephenjm.crypto.spring.cache;

import com.stephenjm.crypto.spring.config.CryptoClientProperties;
import com.stephenjm.crypto.spring.metrics.CacheMetrics;
import com.stephenjm.crypto.spring.model.CachedDataKey;
import com.stephenjm.crypto.spring.model.CacheStatistics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CaffeineDataKeyCache to verify our unique business logic.
 */
class CaffeineDataKeyCacheTest {
    
    private CaffeineDataKeyCache cache;
    private CryptoClientProperties properties;
    private CacheMetrics metrics;
    
    @BeforeEach
    void setUp() {
        properties = new CryptoClientProperties();
        properties.getCache().setMaxSize(100);
        properties.getCache().setTtl(java.time.Duration.ofDays(7));
        
        metrics = new CacheMetrics(new SimpleMeterRegistry());
        cache = new CaffeineDataKeyCache(properties, metrics);
    }
    
    @Test
    void shouldCacheAndRetrieveDataKey() {
        // Given: A valid cached data key
        LocalDateTime now = LocalDateTime.now();
        CachedDataKey dataKey = new CachedDataKey(
            "test-key-1",
            new byte[]{1, 2, 3, 4},
            now,
            now.plusDays(1),
            "v1"
        );
        
        // When: We cache and retrieve it
        cache.cacheDataKey("test-key-1", dataKey);
        Optional<CachedDataKey> retrieved = cache.retrieveDataKey("test-key-1");
        
        // Then: It should be present
        assertTrue(retrieved.isPresent());
        assertEquals("test-key-1", retrieved.get().keyId());
        assertArrayEquals(new byte[]{1, 2, 3, 4}, retrieved.get().encryptedKey());
    }
    
    @Test
    void shouldRejectExpiredKeys() {
        // Given: An already-expired key
        LocalDateTime now = LocalDateTime.now();
        CachedDataKey expiredKey = new CachedDataKey(
            "expired-key",
            new byte[]{1, 2, 3, 4},
            now.minusDays(10),
            now.minusDays(1), // Expired yesterday
            "v1"
        );
        
        // When: We try to cache it
        cache.cacheDataKey("expired-key", expiredKey);
        
        // Then: It should not be cached (our business logic)
        Optional<CachedDataKey> retrieved = cache.retrieveDataKey("expired-key");
        assertFalse(retrieved.isPresent());
    }
    
    @Test
    void shouldReturnEmptyForMissingKey() {
        // When: We retrieve a non-existent key
        Optional<CachedDataKey> retrieved = cache.retrieveDataKey("non-existent");
        
        // Then: It should be empty
        assertFalse(retrieved.isPresent());
    }
    
    @Test
    void shouldEvictSpecificKey() {
        // Given: A cached key
        LocalDateTime now = LocalDateTime.now();
        CachedDataKey dataKey = new CachedDataKey(
            "key-to-evict",
            new byte[]{1, 2, 3, 4},
            now,
            now.plusDays(1),
            "v1"
        );
        cache.cacheDataKey("key-to-evict", dataKey);
        
        // When: We evict it
        cache.evictDataKey("key-to-evict");
        
        // Then: It should no longer be present
        Optional<CachedDataKey> retrieved = cache.retrieveDataKey("key-to-evict");
        assertFalse(retrieved.isPresent());
    }
    
    @Test
    void shouldProvideStatistics() {
        // Given: Some cache operations
        LocalDateTime now = LocalDateTime.now();
        CachedDataKey dataKey = new CachedDataKey(
            "stats-key",
            new byte[]{1, 2, 3, 4},
            now,
            now.plusDays(1),
            "v1"
        );
        
        cache.cacheDataKey("stats-key", dataKey);
        cache.retrieveDataKey("stats-key"); // Hit
        cache.retrieveDataKey("missing-key"); // Miss
        
        // When: We get statistics
        CacheStatistics stats = cache.getStatistics();
        
        // Then: They should reflect operations
        assertTrue(stats.totalRequests() > 0);
        assertTrue(stats.currentSize() <= properties.getCache().getMaxSize());
    }
    
    @Test
    void shouldClearAllKeys() {
        // Given: Multiple cached keys
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 5; i++) {
            CachedDataKey dataKey = new CachedDataKey(
                "key-" + i,
                new byte[]{(byte) i},
                now,
                now.plusDays(1),
                "v1"
            );
            cache.cacheDataKey("key-" + i, dataKey);
        }
        
        // When: We clear the cache
        cache.clearCache();
        
        // Then: All keys should be gone
        for (int i = 0; i < 5; i++) {
            Optional<CachedDataKey> retrieved = cache.retrieveDataKey("key-" + i);
            assertFalse(retrieved.isPresent());
        }
    }
    
    @Test
    void shouldCheckCapacity() {
        // Given: A small cache
        properties.getCache().setMaxSize(10);
        cache = new CaffeineDataKeyCache(properties, metrics);
        
        // When: We fill it partially
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 5; i++) {
            CachedDataKey dataKey = new CachedDataKey(
                "key-" + i,
                new byte[]{(byte) i},
                now,
                now.plusDays(1),
                "v1"
            );
            cache.cacheDataKey("key-" + i, dataKey);
        }
        
        // Then: It should not be near capacity (< 90%)
        assertFalse(cache.isNearCapacity());
    }
}
