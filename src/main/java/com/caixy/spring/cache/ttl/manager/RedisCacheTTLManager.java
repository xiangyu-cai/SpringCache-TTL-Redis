package com.caixy.spring.cache.ttl.manager;

import com.caixy.spring.cache.ttl.holder.CacheTTLHolder;
import java.time.Duration;
import java.util.Optional;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.lang.Nullable;

public class RedisCacheTTLManager extends RedisCacheManager {

  public RedisCacheTTLManager(
      RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
    super(cacheWriter, defaultCacheConfiguration);
  }

  @Override
  protected RedisCache createRedisCache(
      String name, @Nullable RedisCacheConfiguration cacheConfig) {
    /*
    Set up the TTL based on the value in CacheTTLHolder
     */
    return super.createRedisCache(
        name,
        cacheConfig.entryTtl(
            Optional.ofNullable(CacheTTLHolder.getInstance().getTTL())
                .map(Duration::ofMillis)
                .orElse(cacheConfig.getTtl())));
  }
}
