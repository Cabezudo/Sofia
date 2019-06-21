package net.cabezudo.sofia.core.passwords;

import net.cabezudo.sofia.core.WarningSystemException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.15
 */
public class PasswordMaxSizeException extends WarningSystemException {

  public PasswordMaxSizeException(int length) {
    super("Password too long: " + length);
  }
}
