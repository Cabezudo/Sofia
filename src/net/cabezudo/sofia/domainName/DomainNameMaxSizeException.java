package net.cabezudo.sofia.domainName;

import net.cabezudo.sofia.core.WarningSystemException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.15
 */
public class DomainNameMaxSizeException extends WarningSystemException {

  public DomainNameMaxSizeException(int length) {
    super("Domain name too long: " + length);
  }
}
