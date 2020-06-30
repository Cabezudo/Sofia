package net.cabezudo.sofia.core.sic.tokens;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class NumberToken extends Token {

  public NumberToken(String number, Position position) {
    super(number, position);
  }

  @Override
  public boolean isNumber() {
    return true;
  }

  @Override
  public String getCSSClass() {
    return "number";
  }

  @Override
  public TokenType getType() {
    return TokenType.NUMBER;
  }
}
