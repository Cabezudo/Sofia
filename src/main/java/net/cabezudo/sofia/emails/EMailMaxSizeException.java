package net.cabezudo.sofia.emails;

import net.cabezudo.sofia.core.WarningSystemException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.15
 */
public class EMailMaxSizeException extends WarningSystemException {

  public EMailMaxSizeException(int length) {
    super("e-mail too long: " + length);
  }
}
