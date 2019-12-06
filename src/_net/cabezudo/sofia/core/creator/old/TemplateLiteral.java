package net.cabezudo.sofia.core.creator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.30
 */
class TemplateLiteral implements Comparable<TemplateLiteral> {

  private final String key;
  private final String value;

  TemplateLiteral(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  @Override
  public int compareTo(TemplateLiteral o) {
    return this.key.compareTo(o.key);
  }
}
