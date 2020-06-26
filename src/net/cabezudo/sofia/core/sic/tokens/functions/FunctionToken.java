package net.cabezudo.sofia.core.sic.tokens.functions;

import net.cabezudo.sofia.core.sic.tokens.Position;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class FunctionToken extends Token {

  public FunctionToken(String name, Position position) {
    super(name, position);
  }

  @Override
  public boolean isFunction() {
    return true;
  }

}