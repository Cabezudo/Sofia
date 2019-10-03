package net.cabezudo.sofia.hosts;

import net.cabezudo.sofia.emails.ParametrizedException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.06.22
 */
public class HostValidationException extends ParametrizedException {

  public HostValidationException(String messageKey, Object... parameters) {
    super(messageKey, parameters);
  }
}
