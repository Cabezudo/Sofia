package net.cabezudo.sofia.domainName;

import net.cabezudo.sofia.emails.ParametrizedException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.08
 */
public class DomainNameValidationException extends ParametrizedException {

  public DomainNameValidationException(String message, Object... parameters) {
    super(message, parameters);
  }
}
