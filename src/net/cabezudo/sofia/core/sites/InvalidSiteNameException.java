package net.cabezudo.sofia.core.sites;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.21
 */
public class InvalidSiteNameException extends InvalidSiteValueException {

  public InvalidSiteNameException(String message, String value) {
    super(message, value);
  }
}
