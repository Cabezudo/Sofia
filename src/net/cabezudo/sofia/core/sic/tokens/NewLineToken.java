package net.cabezudo.sofia.core.sic.tokens;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class NewLineToken extends Token {

  public NewLineToken(Position position) {
    super("\n", position);
  }

  @Override
  public boolean isNewLine() {
    return true;
  }
}
