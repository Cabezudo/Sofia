package net.cabezudo.sofia.addresses;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.geography.AdministrativeDivision;
import net.cabezudo.sofia.geography.AdministrativeDivisionFactory;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.05
 */
public class Address {

  private String street;
  private String exteriorNumber;
  private String interiorNumber;
  private final AdministrativeDivision parent;
  private Integer postalCode;
  private String reference;

  public Address(String street, String exteriorNumber, String interiorNumber, AdministrativeDivision parent, Integer postalCode, String reference) {
    this.street = street;
    this.exteriorNumber = exteriorNumber;
    this.interiorNumber = interiorNumber;
    this.parent = parent;
    this.postalCode = postalCode;
    this.reference = reference;
  }

  Address(JSONObject o) {
    this.parent = AdministrativeDivisionFactory.get(o);
  }

  public String getStreet() {
    return street;
  }

  public String getExteriorNumber() {
    return exteriorNumber;
  }

  public String getInteriorNumber() {
    return interiorNumber;
  }

  public AdministrativeDivision getParent() {
    return parent;
  }

  public Integer getPostalCode() {
    return postalCode;
  }

  public String getRefernece() {
    return reference;
  }

  public JSONObject toJSONTree() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.add(new JSONPair("street", street));
    jsonObject.add(new JSONPair("exteriorNumber", exteriorNumber));
    jsonObject.add(new JSONPair("interiorNumber", interiorNumber));
    jsonObject.add(new JSONPair(parent.getType(), parent.toJSONTree()));
    jsonObject.add(new JSONPair("postalCode", postalCode));
    jsonObject.add(new JSONPair("reference", reference));
    return jsonObject;
  }

  public JSONObject toWebListTree(Language language) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.add(new JSONPair("street", street));
    jsonObject.add(new JSONPair("exteriorNumber", exteriorNumber));
    jsonObject.add(new JSONPair("interiorNumber", interiorNumber));
    jsonObject.add(new JSONPair(parent.getType(), parent.toJSONTree()));
    jsonObject.add(new JSONPair("postalCode", postalCode));
    jsonObject.add(new JSONPair("reference", reference));
    return jsonObject;
  }

  public JSONObject toMenuTree(Language language) {
    return toWebListTree(language);
  }
}
