package com.unistack.tamboo.mgt.config;

import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */

@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    private  Logger log = LoggerFactory
            .getLogger(CacheConfig.class);


    public static  String SYS_CACHE = "sysCache";

    public static  String USER_CACHE = "userCache";


    @Override
    @Bean
    public CacheManager cacheManager() {
        log.info("Initializing simple Guava Cache manager.");
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        GuavaCache sysCache = new GuavaCache(SYS_CACHE, CacheBuilder.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build());

        GuavaCache userCache = new GuavaCache(USER_CACHE, CacheBuilder.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES).build());

        cacheManager.setCaches(Arrays.asList(sysCache, userCache));

        return cacheManager;
    }

    @Override
    public CacheResolver cacheResolver() {
        return null;
    }

    @Override
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return null;
    }
}
