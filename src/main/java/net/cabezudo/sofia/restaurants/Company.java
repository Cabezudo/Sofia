package net.cabezudo.sofia.restaurants;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.addresses.Address;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.27
 */
public abstract class Company {

  private final String id;
  private final Address address;

  public Company(String id, Address address) {
    this.id = id;
    this.address = address;
  }

  public String getId() {
    return id;
  }

  public Address getAddress() {
    return address;
  }

  public JSONObject toJSONTree() {
    JSONObject jsonRestaurantType = new JSONObject();
    jsonRestaurantType.add(new JSONPair("id", id));
    jsonRestaurantType.add(new JSONPair("address", address.toJSONTree()));
    return jsonRestaurantType;
  }
}
