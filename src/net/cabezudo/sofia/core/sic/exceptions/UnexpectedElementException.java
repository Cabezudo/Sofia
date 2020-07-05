package net.cabezudo.sofia.core.sic.exceptions;

import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class UnexpectedElementException extends SICParseException {

  private static final long serialVersionUID = 1L;

  public UnexpectedElementException(String value, Token token) {
    super("Unexpected element: " + value, token);
  }

  public UnexpectedElementException(String expected, String value, Throwable cause, Token token) {
    super("Unexpected element. Waiting for a " + expected + " and have a " + value + ".", cause, token);
  }

  public UnexpectedElementException(String expected, String value, Token token) {
    this(expected, value, null, token);
  }
}
