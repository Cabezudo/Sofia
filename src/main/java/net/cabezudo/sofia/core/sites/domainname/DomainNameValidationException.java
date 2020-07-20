package net.cabezudo.sofia.core.sites.domainname;

import net.cabezudo.sofia.core.ParametrizedException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.08
 */
public class DomainNameValidationException extends ParametrizedException {

  public DomainNameValidationException(String message, String... parameters) {
    super(message, parameters);
  }
}
