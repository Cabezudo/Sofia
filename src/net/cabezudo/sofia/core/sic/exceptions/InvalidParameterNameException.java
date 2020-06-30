package net.cabezudo.sofia.core.sic.exceptions;

import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class InvalidParameterNameException extends SICParseException {

  private static final long serialVersionUID = 1L;

  public InvalidParameterNameException(Token token) {
    super("Invalid parameter nema " + token.getValue() + ".", token.getPosition());
  }
}
