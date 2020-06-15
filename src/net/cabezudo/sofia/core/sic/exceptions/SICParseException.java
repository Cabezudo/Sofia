package net.cabezudo.sofia.core.sic.exceptions;

import net.cabezudo.sofia.core.sic.tokens.Position;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class SICParseException extends SICCompilerException {

  private static final long serialVersionUID = 1L;

  public SICParseException(String message, Position position) {
    super(message, position);
  }

  public SICParseException(String message, Throwable cause, Position position) {
    super(message, cause, position);
  }
}
