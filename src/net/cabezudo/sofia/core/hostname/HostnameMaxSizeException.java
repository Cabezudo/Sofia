package net.cabezudo.sofia.core.hostname;

import net.cabezudo.sofia.core.WarningSystemException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.15
 */
public class HostnameMaxSizeException extends WarningSystemException {

  public HostnameMaxSizeException(int length) {
    super("Hostname too long: " + length);
  }
}
