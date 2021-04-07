package net.cabezudo.sofia.addresses;

import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.03.25
 */
public class AddressFactory {

  public static Address get(JSONValue jsonAddress) {
    if (jsonAddress.isNull()) {
      throw new SofiaRuntimeException("null parameter for address object.");
    }
    return new Address(jsonAddress.toJSONObject());
  }

}
