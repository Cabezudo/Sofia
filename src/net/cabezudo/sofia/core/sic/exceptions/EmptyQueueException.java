package net.cabezudo.sofia.core.sic.exceptions;

import net.cabezudo.sofia.core.sic.tokens.Position;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class EmptyQueueException extends Exception {

  private static final long serialVersionUID = 1L;
  private final Position position;

  public EmptyQueueException(Position position) {
    super();
    this.position = position;
  }

  public EmptyQueueException(Position position, Throwable cause) {
    super(cause);
    this.position = position;
  }

  public Position getPosition() {
    return position;
  }
}
