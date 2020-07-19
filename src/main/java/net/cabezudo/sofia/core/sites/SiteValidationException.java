package net.cabezudo.sofia.core.sites;

import net.cabezudo.sofia.core.ParametrizedException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.05.01
 */
public class SiteValidationException extends ParametrizedException {

  public SiteValidationException(String message, Object... parameters) {
    super(message, parameters);
  }
}
