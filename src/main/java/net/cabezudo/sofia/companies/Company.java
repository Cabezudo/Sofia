package net.cabezudo.sofia.companies;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.addresses.Address;
import net.cabezudo.sofia.addresses.AddressFactory;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.27
 */
public class Company {

  private final Address address;

  Company(JSONObject o) throws JSONParseException {
    if (!o.hasChilds()) {
      throw new JSONParseException("The object doesn't have properties", o.getPosition());
    }
    this.address = AddressFactory.get(o.getNullObject("address"));
  }

  public Address getAddress() {
    return address;
  }

  public JSONObject toJSONTree() {
    JSONObject jsonRestaurantType = new JSONObject();
    jsonRestaurantType.add(new JSONPair("address", address.toJSONTree()));
    return jsonRestaurantType;
  }
}
