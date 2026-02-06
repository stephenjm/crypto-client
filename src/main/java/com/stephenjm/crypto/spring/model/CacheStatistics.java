package com.stephenjm.crypto.spring.model;

/**
 * Domain model for cache performance metrics.
 * Provides business insights into cache effectiveness and health.
 */
public record CacheStatistics(
    long totalRequests,
    long cacheHits,
    long cacheMisses,
    long evictions,
    long expirations,
    long currentSize,
    long maxSize
) {
    /**
     * Business metric: Calculate cache hit ratio as a percentage.
     */
    public double hitRatio() {
        if (totalRequests == 0) {
            return 0.0;
        }
        return (double) cacheHits / totalRequests * 100.0;
    }
    
    /**
     * Business metric: Calculate cache miss ratio as a percentage.
     */
    public double missRatio() {
        if (totalRequests == 0) {
            return 0.0;
        }
        return (double) cacheMisses / totalRequests * 100.0;
    }
    
    /**
     * Business logic: Determine if cache is performing adequately.
     * Our business rule: hit ratio should be above 80% for good performance.
     */
    public boolean isPerformingWell() {
        return hitRatio() >= 80.0;
    }
    
    /**
     * Business logic: Check if cache is near capacity.
     * Our business rule: warn when cache is 90% full.
     */
    public boolean isNearCapacity() {
        return maxSize > 0 && (double) currentSize / maxSize >= 0.9;
    }
    
    /**
     * Business metric: Get utilization percentage.
     */
    public double utilizationPercentage() {
        if (maxSize == 0) {
            return 0.0;
        }
        return (double) currentSize / maxSize * 100.0;
    }
}
