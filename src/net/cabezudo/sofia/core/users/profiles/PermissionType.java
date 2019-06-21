package net.cabezudo.sofia.core.users.profiles;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.17
 */
public class PermissionType {

  private final int id;
  private final String name;

  public PermissionType(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "[id: " + id + ", namd: " + name + "]";
  }
}
