package net.cabezudo.sofia.core.cache;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.01
 * @param <K>
 * @param <T>
 */
public class Cache<K, T> {

  private final Map<K, T> map = new TreeMap<>();

  public T get(K key) {
    return map.get(key);
  }

  public void put(K key, T value) {
    map.put(key, value);
  }
}
