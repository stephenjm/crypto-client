package com.stephenjm.crypto.spring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;

/**
 * Configuration properties for crypto client with our business-specific settings.
 * Defines our unique operational parameters and policies.
 */
@ConfigurationProperties(prefix = "crypto")
@Validated
public class CryptoClientProperties {
    
    private KmsConfig kms = new KmsConfig();
    private CacheConfig cache = new CacheConfig();
    private FailoverConfig failover = new FailoverConfig();
    private EventsConfig events = new EventsConfig();
    
    public KmsConfig getKms() {
        return kms;
    }
    
    public void setKms(KmsConfig kms) {
        this.kms = kms;
    }
    
    public CacheConfig getCache() {
        return cache;
    }
    
    public void setCache(CacheConfig cache) {
        this.cache = cache;
    }
    
    public FailoverConfig getFailover() {
        return failover;
    }
    
    public void setFailover(FailoverConfig failover) {
        this.failover = failover;
    }
    
    public EventsConfig getEvents() {
        return events;
    }
    
    public void setEvents(EventsConfig events) {
        this.events = events;
    }
    
    /**
     * KMS configuration for our cloud provider integration.
     */
    public static class KmsConfig {
        private String provider = "mock";
        private String endpoint;
        private Duration timeout = Duration.ofSeconds(30);
        
        public String getProvider() {
            return provider;
        }
        
        public void setProvider(String provider) {
            this.provider = provider;
        }
        
        public String getEndpoint() {
            return endpoint;
        }
        
        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }
        
        public Duration getTimeout() {
            return timeout;
        }
        
        public void setTimeout(Duration timeout) {
            this.timeout = timeout;
        }
    }
    
    /**
     * Cache configuration with our business retention policies.
     */
    public static class CacheConfig {
        @Min(100)
        private int maxSize = 1000;
        
        @NotNull
        private Duration ttl = Duration.ofDays(7);
        
        private boolean enabled = true;
        private RedisConfig redis = new RedisConfig();
        
        public int getMaxSize() {
            return maxSize;
        }
        
        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }
        
        public Duration getTtl() {
            return ttl;
        }
        
        public void setTtl(Duration ttl) {
            this.ttl = ttl;
        }
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public RedisConfig getRedis() {
            return redis;
        }
        
        public void setRedis(RedisConfig redis) {
            this.redis = redis;
        }
    }
    
    /**
     * Redis configuration for distributed caching.
     */
    public static class RedisConfig {
        private boolean enabled = false;
        private String host = "localhost";
        private int port = 6379;
        private Duration ttl = Duration.ofDays(7);
        private boolean encryptionEnabled = true;
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public String getHost() {
            return host;
        }
        
        public void setHost(String host) {
            this.host = host;
        }
        
        public int getPort() {
            return port;
        }
        
        public void setPort(int port) {
            this.port = port;
        }
        
        public Duration getTtl() {
            return ttl;
        }
        
        public void setTtl(Duration ttl) {
            this.ttl = ttl;
        }
        
        public boolean isEncryptionEnabled() {
            return encryptionEnabled;
        }
        
        public void setEncryptionEnabled(boolean encryptionEnabled) {
            this.encryptionEnabled = encryptionEnabled;
        }
    }
    
    /**
     * Failover configuration for our degraded mode handling.
     */
    public static class FailoverConfig {
        private boolean enabled = true;
        private int circuitBreakerThreshold = 5;
        private Duration circuitBreakerTimeout = Duration.ofSeconds(60);
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public int getCircuitBreakerThreshold() {
            return circuitBreakerThreshold;
        }
        
        public void setCircuitBreakerThreshold(int circuitBreakerThreshold) {
            this.circuitBreakerThreshold = circuitBreakerThreshold;
        }
        
        public Duration getCircuitBreakerTimeout() {
            return circuitBreakerTimeout;
        }
        
        public void setCircuitBreakerTimeout(Duration circuitBreakerTimeout) {
            this.circuitBreakerTimeout = circuitBreakerTimeout;
        }
    }
    
    /**
     * Events configuration for key rotation synchronization.
     */
    public static class EventsConfig {
        private boolean enabled = false;
        private FallbackConfig fallback = new FallbackConfig();
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public FallbackConfig getFallback() {
            return fallback;
        }
        
        public void setFallback(FallbackConfig fallback) {
            this.fallback = fallback;
        }
    }
    
    /**
     * Fallback configuration for polling-based synchronization.
     */
    public static class FallbackConfig {
        private boolean enabled = false;
        private Duration interval = Duration.ofMinutes(1);
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public Duration getInterval() {
            return interval;
        }
        
        public void setInterval(Duration interval) {
            this.interval = interval;
        }
    }
}
