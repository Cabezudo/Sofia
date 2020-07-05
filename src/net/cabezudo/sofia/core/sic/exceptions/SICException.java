package net.cabezudo.sofia.core.sic.exceptions;

import net.cabezudo.sofia.core.sic.tokens.Position;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public abstract class SICException extends Exception {

  private static final long serialVersionUID = 1L;

  private final Token token;

  public SICException(String message, Token token) {
    super(message);
    this.token = token;
  }

  public SICException(String message, Throwable cause, Token token) {
    super(message, cause);
    this.token = token;
  }

  public String getValue() {
    return token.getValue();
  }

  public Position getPosition() {
    return token.getPosition();
  }

}
