package com.crypto.spring.config;

import com.crypto.security.kms.KmsClient;
import com.crypto.security.provider.MockKmsProvider;
import com.crypto.spring.EnvelopeEncryptionFacade;
import com.crypto.spring.cache.CaffeineDataKeyCache;
import com.crypto.spring.cache.DataKeyCache;
import com.crypto.spring.cache.RedisDataKeyCache;
import com.crypto.spring.cache.RedisEncryptionManager;
import com.crypto.spring.cache.TwoTierCacheStrategy;
import com.crypto.spring.failover.FailureModeController;
import com.crypto.spring.metrics.CacheMetrics;
import com.crypto.spring.model.CachedDataKey;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Optional;

/**
 * Auto-configuration for crypto-client Spring Boot integration.
 * Implements our unique bean creation and dependency wiring logic.
 * Uses conditional annotations to adapt to different deployment scenarios.
 */
@AutoConfiguration
@EnableConfigurationProperties(CryptoClientProperties.class)
@ConditionalOnClass(KmsClient.class)
@ComponentScan(basePackages = "com.crypto.spring")
public class CryptoClientAutoConfiguration {
    
    /**
     * Our unique KMS client bean creation with provider selection.
     */
    @Bean
    @ConditionalOnMissingBean
    public KmsClient kmsClient(CryptoClientProperties properties) {
        // Our business logic: Select provider based on configuration
        String provider = properties.getKms().getProvider();
        
        return switch (provider.toLowerCase()) {
            case "mock" -> KmsClient.builder()
                .provider(new MockKmsProvider(
                    properties.getKms().getEndpoint() != null 
                        ? properties.getKms().getEndpoint() 
                        : "http://localhost:8080"
                ))
                .build();
            // Future: AWS, GCP, Azure providers
            default -> throw new IllegalArgumentException("Unsupported KMS provider: " + provider);
        };
    }
    
    /**
     * Our unique cache metrics bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public CacheMetrics cacheMetrics(MeterRegistry meterRegistry) {
        return new CacheMetrics(meterRegistry);
    }
    
    /**
     * Our unique L1 cache bean (Caffeine).
     */
    @Bean
    @ConditionalOnMissingBean(name = "caffeineDataKeyCache")
    public CaffeineDataKeyCache caffeineDataKeyCache(
            CryptoClientProperties properties,
            CacheMetrics metrics) {
        return new CaffeineDataKeyCache(properties, metrics);
    }
    
    /**
     * Our unique Redis connection factory for L2 cache.
     */
    @Bean
    @ConditionalOnProperty(value = "crypto.cache.redis.enabled", havingValue = "true")
    @ConditionalOnMissingBean
    public RedisConnectionFactory redisConnectionFactory(CryptoClientProperties properties) {
        // Our unique Redis configuration
        LettuceConnectionFactory factory = new LettuceConnectionFactory(
            properties.getCache().getRedis().getHost(),
            properties.getCache().getRedis().getPort()
        );
        factory.afterPropertiesSet();
        return factory;
    }
    
    /**
     * Our unique Redis template for cached data keys.
     */
    @Bean
    @ConditionalOnProperty(value = "crypto.cache.redis.enabled", havingValue = "true")
    @ConditionalOnMissingBean(name = "cachedDataKeyRedisTemplate")
    public RedisTemplate<String, CachedDataKey> cachedDataKeyRedisTemplate(
            RedisConnectionFactory connectionFactory) {
        // Our unique Redis serialization configuration
        RedisTemplate<String, CachedDataKey> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
    
    /**
     * Our unique Redis encryption manager.
     */
    @Bean
    @ConditionalOnProperty(value = "crypto.cache.redis.enabled", havingValue = "true")
    @ConditionalOnMissingBean
    public RedisEncryptionManager redisEncryptionManager(CryptoClientProperties properties) {
        return new RedisEncryptionManager(properties);
    }
    
    /**
     * Our unique L2 cache bean (Redis) - optional.
     */
    @Bean
    @ConditionalOnProperty(value = "crypto.cache.redis.enabled", havingValue = "true")
    @ConditionalOnMissingBean(name = "redisDataKeyCache")
    public RedisDataKeyCache redisDataKeyCache(
            RedisTemplate<String, CachedDataKey> redisTemplate,
            RedisEncryptionManager encryptionManager,
            CacheMetrics metrics,
            CryptoClientProperties properties) {
        return new RedisDataKeyCache(redisTemplate, encryptionManager, metrics, properties);
    }
    
    /**
     * Our unique two-tier cache strategy bean.
     */
    @Bean
    @ConditionalOnMissingBean(DataKeyCache.class)
    public DataKeyCache dataKeyCache(
            CaffeineDataKeyCache l1Cache,
            Optional<RedisDataKeyCache> l2Cache,
            CacheMetrics metrics) {
        // Our unique cache tier composition logic
        return new TwoTierCacheStrategy(l1Cache, l2Cache, metrics);
    }
    
    /**
     * Our unique failure mode controller bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public FailureModeController failureModeController(ApplicationEventPublisher eventPublisher) {
        return new FailureModeController(eventPublisher);
    }
    
    /**
     * Our unique envelope encryption facade bean - the main entry point.
     */
    @Bean
    @ConditionalOnMissingBean
    public EnvelopeEncryptionFacade envelopeEncryptionFacade(
            KmsClient kmsClient,
            DataKeyCache dataKeyCache,
            FailureModeController failureModeController) {
        // Our unique facade creation with dependency injection
        return new EnvelopeEncryptionFacade(kmsClient, dataKeyCache, failureModeController);
    }
}
