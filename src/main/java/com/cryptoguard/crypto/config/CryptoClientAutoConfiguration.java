package com.cryptoguard.crypto.config;

import com.cryptoguard.crypto.cache.DataKeyStore;
import com.cryptoguard.crypto.cache.OffHeapKeyStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoClientAutoConfiguration {
    @Bean
    public DataKeyStore offHeapKeyStore() {
        return new OffHeapKeyStore();
    }
}
