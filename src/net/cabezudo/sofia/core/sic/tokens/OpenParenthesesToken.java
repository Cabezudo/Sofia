package net.cabezudo.sofia.core.sic.tokens;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class OpenParenthesesToken extends Token {

  public OpenParenthesesToken(Position position) {
    super("(", position);
  }

  @Override
  public boolean isOpenParentheses() {
    return true;
  }
}
