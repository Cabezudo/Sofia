package net.cabezudo.hayquecomer.restaurants;

import net.cabezudo.sofia.core.validation.SofiaValidationException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.03.03
 */
public class InvalidValueException extends SofiaValidationException {

  public InvalidValueException(String message) {
    super(message);
  }

}
