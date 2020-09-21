package net.cabezudo.sofia.core.ws.servlet.services;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.18
 */
public class QueryParameters {

  private Map<String, String> map = new TreeMap<>();

  void put(String name, String value) {
    map.put(name, value);
  }

  public String get(String key) {
    return map.get(key);
  }
}
