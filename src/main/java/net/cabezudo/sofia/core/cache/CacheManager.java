package net.cabezudo.sofia.core.cache;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.01
 */
public class CacheManager {

  private static final CacheManager INSTANCE = new CacheManager();
  private final Map<String, Cache> map = new TreeMap<>();

  private CacheManager() {
    // Nothing to do here
  }

  public static CacheManager getInstance() {
    return INSTANCE;
  }

  public Cache getCache(String cacheName) {
    Cache cache = map.get(cacheName);
    if (cache == null) {
      cache = new Cache();
      map.put(cacheName, cache);
    }
    return cache;
  }
}
