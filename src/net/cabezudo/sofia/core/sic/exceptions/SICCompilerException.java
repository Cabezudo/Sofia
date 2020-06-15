package net.cabezudo.sofia.core.sic.exceptions;

import net.cabezudo.sofia.core.sic.tokens.Position;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class SICCompilerException extends SICException {

  private static final long serialVersionUID = 1L;

  public SICCompilerException(String message, Position position) {
    super(message, position);
  }

  public SICCompilerException(Throwable cause, Position position) {
    super(cause.getMessage(), cause, position);
  }

  public SICCompilerException(String message, Throwable cause, Position position) {
    super(message, cause, position);
  }
}
