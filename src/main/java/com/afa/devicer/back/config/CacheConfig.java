package com.afa.devicer.back.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@NoArgsConstructor
@SuppressWarnings({"PMD.CommentRequired", "PMD.LawOfDemeter", "PMD.TooManyMethods"})
public class CacheConfig {

    public static final String CACHE_PRODUCT_CATEGORY = "cacheProductCategory";

    @Value("${cache.v2.ttl.hours}")
    private int cacheV2TtlHours;

    @Bean
    public Cache cacheProductCategory() {
        return new CaffeineCache(CACHE_PRODUCT_CATEGORY,
                Caffeine.newBuilder()
                        .expireAfterWrite(cacheV2TtlHours, TimeUnit.HOURS)
                        .maximumSize(100)
                        .build());
    }
}
