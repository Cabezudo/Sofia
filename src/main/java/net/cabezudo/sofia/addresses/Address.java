package net.cabezudo.sofia.addresses;

import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.settlements.Settlement;
import net.cabezudo.sofia.streets.Street;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.05
 */
public class Address {

  private final Street street;
  private final String exteriorNumber;
  private final String interiorNumber;
  private final Settlement settlement;
  private final Integer postalCode;
  private final String reference;

  public Address(Street street, String exteriorNumber, String interiorNumber, Settlement settlement, Integer postalCode, String reference) {
    this.street = street;
    this.exteriorNumber = exteriorNumber;
    this.interiorNumber = interiorNumber;
    this.settlement = settlement;
    this.postalCode = postalCode;
    this.reference = reference;
  }

  public Street getStreet() {
    return street;
  }

  public String getExteriorNumber() {
    return exteriorNumber;
  }

  public String getInteriorNumber() {
    return interiorNumber;
  }

  public Settlement getSettlement() {
    return settlement;
  }

  public Integer getPostalCode() {
    return postalCode;
  }

  public String getRefernece() {
    return reference;
  }

  public JSONObject toJSONTree() {
    return new JSONObject();
  }
}
