package net.cabezudo.sofia.core.sic.exceptions;

import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class UnexpectedTokenException extends SICException {

  private static final long serialVersionUID = 1L;

  public UnexpectedTokenException(String value, Token token) {
    super("Unexpected: " + value, token);
  }

  public UnexpectedTokenException(String expected, String value, Throwable cause, Token token) {
    super("Unexpected " + value + ". Waiting for a " + expected + ".", cause, token);
  }

  public UnexpectedTokenException(String expected, String value, Token token) {
    this(expected, value, null, token);
  }
}
