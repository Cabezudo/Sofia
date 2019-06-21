package net.cabezudo.sofia.postalcodes;

import net.cabezudo.sofia.settlements.Settlement;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.05
 */
public class PostalCode {

  private final int id;
  private final Settlement settlement;
  private final int postalCode;

  public PostalCode(int id, Settlement settlement, Integer postalCode) {
    this.id = id;
    this.settlement = settlement;
    this.postalCode = postalCode;
  }

  @Override
  public String toString() {
    return "[id = " + getId() + ", settlement= " + getSettlement() + ", postalCode = " + getPostalCode() + "]";
  }

  public int getId() {
    return id;
  }

  public Settlement getSettlement() {
    return settlement;
  }

  public Integer getPostalCode() {
    return postalCode;
  }
}
