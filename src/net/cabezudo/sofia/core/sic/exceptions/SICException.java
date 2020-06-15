package net.cabezudo.sofia.core.sic.exceptions;

import net.cabezudo.sofia.core.sic.tokens.Position;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public abstract class SICException extends Exception {

  private static final long serialVersionUID = 1L;

  private final Position position;

  public SICException(String message, Position position) {
    super(message);
    this.position = position;
  }

  public SICException(String message, Throwable cause, Position position) {
    super(message, cause);
    this.position = position;
  }

  public Position getPosition() {
    return position;
  }

}
