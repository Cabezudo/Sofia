package net.cabezudo.sofia.core.sic.elements;

import net.cabezudo.sofia.core.sic.exceptions.SICCompilerException;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.07.03
 */
public class SICCompileTimeException extends SICCompilerException {

  private static final long serialVersionUID = 1L;

  public SICCompileTimeException(String message, Token token) {
    super(message, token);
  }

  public SICCompileTimeException(String message, Throwable cause, Token token) {
    super(message, cause, token);
  }
}
