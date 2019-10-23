package net.cabezudo.sofia.emails;

import net.cabezudo.sofia.core.ParametrizedException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.08
 */
public class EMailAddressValidationException extends ParametrizedException {

  public EMailAddressValidationException(String messageKey, Object... os) {
    super(messageKey, os);
  }
}
