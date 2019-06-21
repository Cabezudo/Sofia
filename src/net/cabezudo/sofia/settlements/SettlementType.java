package net.cabezudo.sofia.settlements;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.29
 */
public class SettlementType {

  private final int id;
  private final String name;

  public SettlementType(int id, String name) {
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
