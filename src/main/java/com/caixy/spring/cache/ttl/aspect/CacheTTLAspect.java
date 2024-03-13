package com.caixy.spring.cache.ttl.aspect;

import com.caixy.spring.cache.ttl.annotation.CacheTTL;
import com.caixy.spring.cache.ttl.holder.CacheTTLHolder;
import java.util.Random;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(-1)
@Component
public class CacheTTLAspect {

  private Random random = new Random();

  @Around("@annotation(cacheTTL)")
  public Object around(ProceedingJoinPoint joinPoint, CacheTTL cacheTTL) throws Throwable {
    long expectedTTL = cacheTTL.value() * 1000L;
    if (expectedTTL <= 0) {
      throw new IllegalArgumentException("ttl must be positive, but was set to " + expectedTTL);
    }
    double fluctuation = cacheTTL.fluctuationRange();
    long ttl = 0L;
    if (fluctuation < 0) {
      throw new IllegalArgumentException(
          "fluctuationRange must be positive, but was set to " + fluctuation);
    } else if (fluctuation == 0.0) {
      ttl = expectedTTL;
    } else {
      ttl = expectedTTL + (long) (random.nextDouble() * fluctuation * 1000L);
    }
    CacheTTLHolder.getInstance().setTTL(ttl);
    try {
      return joinPoint.proceed();
    } finally {
      CacheTTLHolder.getInstance().removeTTL();
    }
  }
}
