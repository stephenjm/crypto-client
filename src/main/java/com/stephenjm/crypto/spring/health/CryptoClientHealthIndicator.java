package com.stephenjm.crypto.spring.health;

import com.stephenjm.crypto.spring.cache.DataKeyCache;
import com.stephenjm.crypto.spring.failover.FailureModeController;
import com.stephenjm.crypto.spring.model.CacheStatistics;
import com.stephenjm.crypto.spring.model.FailureMode;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Health indicator for crypto client operations.
 * Implements our unique health check logic based on operational mode and cache performance.
 */
@Component
public class CryptoClientHealthIndicator implements HealthIndicator {
    
    private final DataKeyCache dataKeyCache;
    private final FailureModeController failureModeController;
    
    public CryptoClientHealthIndicator(
            DataKeyCache dataKeyCache,
            FailureModeController failureModeController) {
        this.dataKeyCache = dataKeyCache;
        this.failureModeController = failureModeController;
    }
    
    @Override
    public Health health() {
        // Our unique health determination logic
        FailureMode mode = failureModeController.getCurrentMode();
        CacheStatistics stats = dataKeyCache.getStatistics();
        
        Health.Builder builder = switch (mode) {
            case NORMAL -> Health.up()
                .withDetail("mode", "NORMAL")
                .withDetail("status", "All operations available");
                
            case DEGRADED -> Health.status("DEGRADED")
                .withDetail("mode", "DEGRADED")
                .withDetail("status", "Decrypt-only mode with cached keys");
                
            case LOCKED -> Health.down()
                .withDetail("mode", "LOCKED")
                .withDetail("status", "No operations allowed");
        };
        
        // Add our cache performance metrics
        return builder
            .withDetail("cacheHitRatio", String.format("%.2f%%", stats.hitRatio()))
            .withDetail("cacheUtilization", String.format("%.2f%%", stats.utilizationPercentage()))
            .withDetail("cachePerformingWell", stats.isPerformingWell())
            .withDetail("circuitBreakerState", failureModeController.getCircuitBreakerState())
            .build();
    }
}
