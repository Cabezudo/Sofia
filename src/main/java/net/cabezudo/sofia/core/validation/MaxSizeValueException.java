package net.cabezudo.sofia.core.validation;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.03.03
 */
public class MaxSizeValueException extends SofiaWarningValidationException {

  private final int length;

  public MaxSizeValueException(int length) {
    this.length = length;
  }

  public int getLength() {
    return length;
  }
}
