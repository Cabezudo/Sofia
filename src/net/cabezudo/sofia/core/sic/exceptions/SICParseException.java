package net.cabezudo.sofia.core.sic.exceptions;

import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class SICParseException extends SICCompilerException {

  private static final long serialVersionUID = 1L;

  public SICParseException(String message, Token token) {
    super(message, token);
  }

  public SICParseException(String message, Throwable cause, Token token) {
    super(message, cause, token);
  }
}
