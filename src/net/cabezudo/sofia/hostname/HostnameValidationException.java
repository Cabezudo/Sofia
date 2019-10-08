package net.cabezudo.sofia.hostname;

import net.cabezudo.sofia.emails.ParametrizedException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.08
 */
public class HostnameValidationException extends ParametrizedException {

  public HostnameValidationException(String message, Object... parameters) {
    super(message, parameters);
  }
}
