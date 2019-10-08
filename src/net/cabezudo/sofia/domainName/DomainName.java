package net.cabezudo.sofia.domainName;

import java.security.InvalidParameterException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.05
 */
public class DomainName {

  public static final int NAME_MAX_LENGTH = 100;
  private final int id;
  private final int siteId;
  private final String name;

  public DomainName(int id, int siteId, String name) {
    this.id = id;
    this.siteId = siteId;
    this.name = name;
    if (name == null) {
      throw new InvalidParameterException("null name");
    }
  }

  public Integer getId() {
    return id;
  }

  public Integer getSiteId() {
    return siteId;
  }

  public String getName() {
    return name;
  }

  char charAt(int i) {
    return name.charAt(i);
  }

  int length() {
    return name.length();
  }

  boolean isEmpty() {
    return name.isEmpty();
  }

  @Override
  public String toString() {
    return "[ id = " + id + ", name = " + name + " ]";
  }

  String toJSON() {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    sb.append("\"id\": ");
    sb.append(id);
    sb.append(", ");
    sb.append("\"name\": \"");
    sb.append(name);
    sb.append("\"}");
    return sb.toString();
  }
}
