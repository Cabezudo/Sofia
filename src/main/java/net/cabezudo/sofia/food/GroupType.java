package net.cabezudo.sofia.food;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.02
 */
public class GroupType {

  public static final GroupType BASE = new GroupType(1, "Base");
  public static final GroupType FROM = new GroupType(2, "from");
  public static final GroupType JUST = new GroupType(3, "just");
  public static final GroupType UP_TO = new GroupType(4, "upTo");
  private final int id;
  private final String name;

  private GroupType(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
