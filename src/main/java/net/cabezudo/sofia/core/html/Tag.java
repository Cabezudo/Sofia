package net.cabezudo.sofia.core.html;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.06.18
 */
public abstract class Tag {

  private String name;
  private final Map<String, Property> properties = new HashMap<>();
  private final int column;

  protected Tag(String name, String data, int column) {
    this.name = name;
    this.column = column;
    String[] ps = data.trim().split("\\s");
    // TODO Improve the parser. Fail with <section file="/  manager/header.html"></section>
    for (String property : ps) {
      String[] p = property.split("=");
      String propertyName = p[0];
      String propertyValue = p[1].substring(1, p[1].length() - 1);
      // TODO Parse the tag in order to get the column position of the value of the property
      properties.put(propertyName, new Property(propertyValue, 0));
    }
  }

  @Override
  public String toString() {
    return getStartTag() + getEndTag();
  }

  public String getStartTag() {
    StringBuilder sb = new StringBuilder();
    sb.append("<").append(name);
    for (Entry<String, Property> entry : properties.entrySet()) {
      if ("template".equals(entry.getKey()) || "file".equals(entry.getKey())) {
        continue;
      }
      Property property = entry.getValue();
      sb.append(" ").append(entry.getKey()).append("=\"").append(property.getValue()).append("\"");
    }
    sb.append(">");
    return sb.toString();
  }

  public String getEndTag() {
    return "</" + name + ">";
  }

  public boolean isSection() {
    return false;
  }

  public String getValue(String propertyName) {
    if (properties.get(propertyName) == null) {
      return null;
    }
    return properties.get(propertyName).getValue();
  }

  public String getId() {
    return getValue("id");
  }

  public int getColumn() {
    return column;
  }

  public void rename(String name) {
    this.name = name;
  }

  public int getColumnValue(String propertyName) {
    return properties.get(propertyName).getColumn();
  }
}
