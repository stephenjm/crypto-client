package com.crypto.security.config;

import com.crypto.security.cache.DataKeyStore;
import com.crypto.security.cache.OffHeapKeyStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoClientAutoConfiguration {
    @Bean
    public DataKeyStore offHeapKeyStore() {
        return new OffHeapKeyStore();
    }
}
