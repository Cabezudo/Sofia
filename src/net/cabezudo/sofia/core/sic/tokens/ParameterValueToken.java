package net.cabezudo.sofia.core.sic.tokens;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class ParameterValueToken extends Token {

  public ParameterValueToken(String value, Position position) {
    super(value, position);
  }

  @Override
  public String getCSSClass() {
    return "parameterValue";
  }

  @Override
  public TokenType getType() {
    return TokenType.PARAMETER_VALUE;
  }
}
