package net.cabezudo.sofia.core.sic.exceptions;

import net.cabezudo.sofia.core.sic.tokens.Position;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class EOSException extends SICParseException {

  private static final long serialVersionUID = 1L;

  public EOSException(Position position) {
    super("Unexpected end of string", position);
  }
}
