package net.cabezudo.sofia.core.http.domains;

import java.util.Map;
import java.util.TreeMap;
import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.24
 */
public class DomainName {

  private final String name;
  private final String[] labels;
  private final Map<String, String> map = new TreeMap<>();

  public DomainName(String name) {
    this.name = name;
    labels = name.split("\\.");
  }

  public boolean match(String pattern) {
    String[] patternElements = pattern.split("\\.");
    Map<String, String> temporalMap = new TreeMap<>();
    for (int i = 0; i < labels.length && i < patternElements.length; i++) {
      if ("**".equals(patternElements[i])) {
        map.putAll(temporalMap);
        return true;
      }
      int patternElementLength = patternElements[i].length();
      if (patternElements[i].charAt(0) == '{') {
        if (patternElements[i].charAt(patternElementLength - 1) != '}') {
          throw new SofiaRuntimeException("Invalid pattern " + patternElements[i] + "' in pattern '" + pattern + "'.");
        }
        String key = patternElements[i].substring(1, patternElementLength - 1);
        String value = labels[i];
        temporalMap.put(key, value);
        continue;
      }
      if (!"*".equals(patternElements[i]) && !labels[i].equals(patternElements[i])) {
        return false;
      }
    }
    if (labels.length != patternElements.length) {
      return false;
    }
    map.putAll(temporalMap);
    return true;
  }

  public DomainName parent() {
    StringBuilder sb = new StringBuilder(name.length());
    for (int i = 1; i < labels.length; i++) {
      sb.append(labels[i]).append(".");
    }
    sb = Utils.chop(sb, 1);
    return new DomainName(sb.toString());
  }

  @Override
  public String toString() {
    return name;
  }

  public String getValue(String key) {
    return map.get(key);
  }
}
