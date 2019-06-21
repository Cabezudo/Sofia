package net.cabezudo.sofia.core.ws.parser.tokens;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.15
 */
public class VariableToken extends Token {

  private final String value;

  public VariableToken(String value) {
    this.value = value.substring(1, value.length() - 1);
  }

  @Override
  public boolean isVariable() {
    return true;
  }

  @Override
  public String toString() {
    return "{" + value + "}";
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public Integer toInteger() {
    throw new ClassCastException("Can't convert a variable to integer");
  }

  @Override
  public Long toLong() {
    throw new ClassCastException("Can't convert a variable to long");
  }
}
