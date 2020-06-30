package net.cabezudo.sofia.core.sic.tokens;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class ParameterNameToken extends Token {

  public ParameterNameToken(String name, Position position) {
    super(name, position);
  }

  @Override
  public boolean isParameterName() {
    return true;
  }

  @Override
  public String getCSSClass() {
    return "parameterName";
  }

  @Override
  public TokenType getType() {
    return TokenType.PARAMETER_NAME;
  }

}
