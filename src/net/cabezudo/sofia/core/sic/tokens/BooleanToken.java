package net.cabezudo.sofia.core.sic.tokens;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class BooleanToken extends Token {

  public BooleanToken(String value, Position position) {
    super(value, position);
  }

  @Override
  public boolean isBoolean() {
    return true;
  }

  @Override
  public String getCSSClass() {
    return "boolean";
  }

  @Override
  public TokenType getType() {
    return TokenType.BOOLEAN;
  }
}
