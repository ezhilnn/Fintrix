// ================================================================
// FILE 1: CacheService.java
// ================================================================
package com.fintrix.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * CacheService
 *
 * Utility service to manually manage Redis cache.
 * Used when you need to evict cache programmatically
 * without @CacheEvict annotation (e.g. from admin endpoints).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final CacheManager cacheManager;

    public void evictUserCache(String userId) {
        evict("user-profile",       userId);
        evict("financial-health",   userId);
        evict("loan-eligibility",   userId);
        evict("card-recommendation",userId);
        log.info("All caches evicted for userId: {}", userId);
    }

    public void evict(String cacheName, String key) {
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
            log.debug("Evicted cache: {} key: {}", cacheName, key);
        }
    }
}


