package com.caixy.spring.cache.ttl.holder;

import java.util.ArrayDeque;
import java.util.Deque;

public class CacheTTLHolder {

  private static final CacheTTLHolder INSTANCE = new CacheTTLHolder();

  private CacheTTLHolder() {}

  public static CacheTTLHolder getInstance() {
    return INSTANCE;
  }

  private final ThreadLocal<Deque<Long>> threadLocal = ThreadLocal.withInitial(ArrayDeque::new);

  public void setTTL(long ttl) {
    Deque<Long> deque = threadLocal.get();
    deque.push(ttl);
  }

  public Long getTTL() {
    Deque<Long> deque = threadLocal.get();
    return deque.peek();
  }

  public void removeTTL() {
    Deque<Long> deque = threadLocal.get();
    deque.pop();
  }
}
