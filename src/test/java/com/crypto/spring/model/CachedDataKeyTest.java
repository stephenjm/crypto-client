package com.crypto.spring.model;

import com.crypto.spring.model.CachedDataKey;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CachedDataKey to verify our business logic.
 */
class CachedDataKeyTest {
    
    @Test
    void shouldDetectExpiredKey() {
        // Given: An expired key
        LocalDateTime now = LocalDateTime.now();
        CachedDataKey expiredKey = new CachedDataKey(
            "test-key",
            new byte[]{1, 2, 3},
            now.minusDays(10),
            now.minusDays(1), // Expired yesterday
            "v1"
        );
        
        // When/Then: It should be detected as expired
        assertTrue(expiredKey.isExpired());
        assertEquals(0, expiredKey.getRemainingTtlSeconds());
    }
    
    @Test
    void shouldDetectValidKey() {
        // Given: A valid key
        LocalDateTime now = LocalDateTime.now();
        CachedDataKey validKey = new CachedDataKey(
            "test-key",
            new byte[]{1, 2, 3},
            now,
            now.plusDays(7), // Expires in 7 days
            "v1"
        );
        
        // When/Then: It should not be expired
        assertFalse(validKey.isExpired());
        assertTrue(validKey.getRemainingTtlSeconds() > 0);
    }
    
    @Test
    void shouldCalculateRemainingTtl() {
        // Given: A key expiring in the future
        LocalDateTime now = LocalDateTime.now();
        CachedDataKey key = new CachedDataKey(
            "test-key",
            new byte[]{1, 2, 3},
            now,
            now.plusHours(1), // Expires in 1 hour
            "v1"
        );
        
        // When/Then: TTL should be approximately 3600 seconds
        long ttl = key.getRemainingTtlSeconds();
        assertTrue(ttl > 3500 && ttl <= 3600);
    }
    
    @Test
    void shouldCompareEqual() {
        // Given: Two identical keys
        LocalDateTime now = LocalDateTime.now();
        CachedDataKey key1 = new CachedDataKey(
            "test-key",
            new byte[]{1, 2, 3},
            now,
            now.plusDays(1),
            "v1"
        );
        CachedDataKey key2 = new CachedDataKey(
            "test-key",
            new byte[]{1, 2, 3},
            now,
            now.plusDays(1),
            "v1"
        );
        
        // When/Then: They should be equal
        assertEquals(key1, key2);
        assertEquals(key1.hashCode(), key2.hashCode());
    }
    
    @Test
    void shouldNotLeakKeyMaterialInToString() {
        // Given: A key with sensitive data
        CachedDataKey key = new CachedDataKey(
            "test-key",
            new byte[]{1, 2, 3, 4, 5, 6, 7, 8},
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            "v1"
        );
        
        // When: We convert to string
        String str = key.toString();
        
        // Then: It should not contain the actual key bytes
        assertFalse(str.contains("[1, 2, 3"));
        assertTrue(str.contains("encryptedKeyLength=8"));
    }
}
