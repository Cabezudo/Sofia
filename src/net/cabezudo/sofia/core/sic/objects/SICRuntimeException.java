package net.cabezudo.sofia.core.sic.objects;

import net.cabezudo.sofia.core.sic.exceptions.SICException;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class SICRuntimeException extends SICException {

  private static final long serialVersionUID = 1L;

  public SICRuntimeException(String message, Token token) {
    super(message, token);
  }
}
