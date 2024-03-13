package com.caixy.spring.cache.ttl.annotation;

import java.lang.annotation.*;

/** The annotation of TTL for a Redis cache. Please use it with {@code @Cacheable}. */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheTTL {

  /** The TTL (seconds) of the cache. */
  long value();

  /**
   * The fluctuation range (seconds) of the TTL, which is to prevent the caches from being
   * invalidated in the same time. The default value is 10 milliseconds.
   */
  double fluctuationRange() default 0.01;
}
