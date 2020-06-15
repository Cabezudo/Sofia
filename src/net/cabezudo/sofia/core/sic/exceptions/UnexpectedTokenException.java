package net.cabezudo.sofia.core.sic.exceptions;

import net.cabezudo.sofia.core.sic.tokens.Position;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class UnexpectedTokenException extends SICException {

  private static final long serialVersionUID = 1L;

  public UnexpectedTokenException(String value, Position position) {
    super("Unexpected: " + value, position);
  }

  public UnexpectedTokenException(String expected, String value, Throwable cause, Position position) {
    super("Unexpected " + value + ". Waiting for a " + expected + ".", cause, position);
  }

  public UnexpectedTokenException(String expected, String value, Position position) {
    this(expected, value, null, position);
  }
}
