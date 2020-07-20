package net.cabezudo.sofia.core.sites.validators;

import net.cabezudo.sofia.core.ParametrizedException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.05.01
 */
public class SiteVersionException extends ParametrizedException {

  public SiteVersionException(String message, String... parameters) {
    super(message, parameters);
  }

}
