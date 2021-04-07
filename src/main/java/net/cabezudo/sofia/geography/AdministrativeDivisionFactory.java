package net.cabezudo.sofia.geography;

import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.07
 */
public class AdministrativeDivisionFactory {

  public static AdministrativeDivision get(JSONObject o) {
    throw new SofiaRuntimeException("Can't find an administrative division property for object. " + o.getPosition());
  }
}
