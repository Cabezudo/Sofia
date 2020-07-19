package net.cabezudo.sofia.core.sites.validators;

import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.sites.SiteValidationException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.04.30
 */
public class SiteNameValidator {

  private static SiteNameValidator INSTANCE;

  public static SiteNameValidator getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new SiteNameValidator();
    }
    return INSTANCE;
  }

  public String validate(String name) throws SiteValidationException {
    try {
      SiteManager.getInstance().validateName(name);
    } catch (EmptySiteNameException e) {
      throw new SiteValidationException("site.name.empty");
    }
    return "hostname.ok";
  }
}
