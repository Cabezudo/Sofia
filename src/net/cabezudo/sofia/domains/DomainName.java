package net.cabezudo.sofia.domains;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.05
 */
public class DomainName {

  public static final int NAME_MAX_LENGTH = 100;
  private final Integer id;
  private final String name;

  public DomainName(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public Integer getId() {
    return id;
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
}
