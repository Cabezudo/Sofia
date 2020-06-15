package net.cabezudo.sofia.core.sic;

import net.cabezudo.sofia.core.sic.tokens.Position;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class StringToken extends Token {

  public StringToken(String string, Position position) {
    super(string, position);
  }

}
