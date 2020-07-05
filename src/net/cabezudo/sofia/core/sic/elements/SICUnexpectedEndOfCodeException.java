package net.cabezudo.sofia.core.sic.elements;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.07.04
 */
public class SICUnexpectedEndOfCodeException extends Exception {

  private static final long serialVersionUID = 1L;

  public SICUnexpectedEndOfCodeException(Throwable e) {
    super("Unexpected end of code.");
  }
}
