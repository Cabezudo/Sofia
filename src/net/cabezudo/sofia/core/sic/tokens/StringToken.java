package net.cabezudo.sofia.core.sic.tokens;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class StringToken extends Token {

  public StringToken(String string, Position position) {
    super(string, position);
  }

  @Override
  public String getCSSClass() {
    return "string";
  }

  @Override
  public TokenType getType() {
    return TokenType.STRING;
  }
}
