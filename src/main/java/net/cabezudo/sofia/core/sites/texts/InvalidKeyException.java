package net.cabezudo.sofia.core.sites.texts;

import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.13
 */
public class InvalidKeyException extends SofiaRuntimeException {

  public InvalidKeyException(String message) {
    super(message);
  }
}
