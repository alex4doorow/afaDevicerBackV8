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
    public static final String CACHE_PRODUCT = "cacheProduct";
    public static final String CACHE_PRODUCT_SUGGEST = "cacheProductSuggest";
    public static final String CACHE_COUNTRY = "cacheCountry";
    public static final String CACHE_CDEK_ACCESS = "cacheCdekAccess";

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

    @Bean
    public Cache cacheProduct() {
        return new CaffeineCache(CACHE_PRODUCT,
                Caffeine.newBuilder()
                        .expireAfterWrite(cacheV2TtlHours, TimeUnit.HOURS)
                        .maximumSize(10_000)
                        .build());
    }

    @Bean
    public Cache cacheProductSuggest() {
        return new CaffeineCache(CACHE_PRODUCT_SUGGEST,
                Caffeine.newBuilder()
                        .expireAfterWrite(cacheV2TtlHours, TimeUnit.HOURS)
                        .maximumSize(10_000)
                        .build());
    }

    @Bean
    public Cache cacheCountry() {
        return new CaffeineCache(CACHE_COUNTRY,
                Caffeine.newBuilder()
                        .expireAfterWrite(cacheV2TtlHours, TimeUnit.HOURS)
                        .maximumSize(100)
                        .build());
    }

    @Bean
    public Cache cacheCdekAccess() {
        return new CaffeineCache(CACHE_CDEK_ACCESS,
                Caffeine.newBuilder()
                        .expireAfterWrite(50, TimeUnit.MINUTES)
                        .maximumSize(10)
                        .build());
    }
}
