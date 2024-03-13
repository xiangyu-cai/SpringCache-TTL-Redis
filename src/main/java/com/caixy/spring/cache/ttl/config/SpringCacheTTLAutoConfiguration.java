package com.caixy.spring.cache.ttl.config;

import com.caixy.spring.cache.ttl.aspect.CacheTTLAspect;
import com.caixy.spring.cache.ttl.manager.RedisCacheTTLManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@Import({RedisValueSerializerConfiguration.class, CacheTTLAspect.class})
@EnableCaching
public class SpringCacheTTLAutoConfiguration implements CachingConfigurer {

  @Value("${spring.application.name:unknown}")
  private String appName;

  private final RedisConnectionFactory redisConnectionFactory;

  private final RedisSerializer<Object> redisValueSerializer;

  public SpringCacheTTLAutoConfiguration(
      RedisConnectionFactory redisConnectionFactory, RedisSerializer<Object> redisValueSerializer) {
    this.redisConnectionFactory = redisConnectionFactory;
    this.redisValueSerializer = redisValueSerializer;
  }

  @Override
  @Bean
  public CacheManager cacheManager() {
    /*
    The default TTL is 2 minutes, and the key name would be "applicationName:keyName"
     */
    RedisCacheConfiguration redisConfiguration =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(java.time.Duration.ofMinutes(2))
            //            .disableCachingNullValues()
            .computePrefixWith(key -> appName + ":" + key + ":")
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    RedisSerializer.string()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    this.redisValueSerializer));
    return new RedisCacheTTLManager(
        RedisCacheWriter.nonLockingRedisCacheWriter(this.redisConnectionFactory),
        redisConfiguration);
  }

  /*
   * The default key generator would take effect when the key were not set in the @Cacheable annotation
   */
  @Override
  public KeyGenerator keyGenerator() {
    return (target, method, params) -> {
      StringBuilder sb = new StringBuilder();
      sb.append(target.getClass().getName());
      sb.append(":");
      sb.append(method.getName());
      sb.append(":");
      for (Object param : params) {
        sb.append(param);
        sb.append(":");
      }
      return sb.substring(0, sb.length() - 1);
    };
  }

  @Bean
  public <T> RedisTemplate<String, T> redisTemplate() {
    RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
    redisTemplate.setKeySerializer(RedisSerializer.string());
    redisTemplate.setValueSerializer(this.redisValueSerializer);
    redisTemplate.setConnectionFactory(this.redisConnectionFactory);
    return redisTemplate;
  }
}
