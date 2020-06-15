package net.cabezudo.sofia.core.sic.exceptions;

import net.cabezudo.sofia.core.sic.tokens.Position;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class UnexpectedElementException extends SICParseException {

  private static final long serialVersionUID = 1L;

  public UnexpectedElementException(String value, Position position) {
    super("Unexpected element: " + value, position);
  }

  public UnexpectedElementException(String expected, String value, Throwable cause, Position position) {
    super("Unexpected element. Waiting for a " + expected + " and have a " + value + ".", cause, position);
  }

  public UnexpectedElementException(String expected, String value, Position position) {
    this(expected, value, null, position);
  }
}
