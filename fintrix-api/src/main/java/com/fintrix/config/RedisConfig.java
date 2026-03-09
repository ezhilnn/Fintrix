package com.fintrix.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * RedisConfig
 *
 * Configures how data is stored and expired in Redis cache.
 *
 * Why Redis for caching?
 *  - User profile, financial score fetched on every dashboard load
 *  - Without cache: every request hits PostgreSQL
 *  - With cache: first request hits DB, rest served from Redis
 *  - Redis is in-memory → microsecond response vs millisecond DB
 *
 * Cache TTL (Time To Live) strategy:
 *  user-profile        → 1 hour   (changes rarely)
 *  financial-health    → 6 hours  (recalculated by job daily)
 *  loan-eligibility    → 30 mins  (user may update profile)
 *  card-recommendation → 30 mins  (same reason)
 */
@Configuration
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory) {

        // ── Default config for all caches ─────────────────────
        RedisCacheConfiguration defaultConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(30))
                        .serializeKeysWith(
                                RedisSerializationContext
                                        .SerializationPair
                                        .fromSerializer(new StringRedisSerializer()))
                        .serializeValuesWith(
                                RedisSerializationContext
                                        .SerializationPair
                                        .fromSerializer(
                                                new GenericJackson2JsonRedisSerializer()))
                        .disableCachingNullValues();

        // ── Per-cache TTL overrides ────────────────────────────
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();

        cacheConfigs.put("user-profile",
                defaultConfig.entryTtl(Duration.ofHours(1)));

        cacheConfigs.put("financial-health",
                defaultConfig.entryTtl(Duration.ofHours(6)));

        cacheConfigs.put("loan-eligibility",
                defaultConfig.entryTtl(Duration.ofMinutes(30)));

        cacheConfigs.put("card-recommendation",
                defaultConfig.entryTtl(Duration.ofMinutes(30)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
}