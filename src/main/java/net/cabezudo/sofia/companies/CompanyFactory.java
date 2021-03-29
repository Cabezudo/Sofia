package net.cabezudo.sofia.companies;

import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONValue;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.03.25
 */
public class CompanyFactory {

  private CompanyFactory() {
    // Utility classes should not have public constructors
  }

  public static Company get(JSONValue jsonCompany) throws JSONParseException {
    if (jsonCompany.isNull()) {
      return null;
    }
    return new Company(jsonCompany.toJSONObject());
  }

}
