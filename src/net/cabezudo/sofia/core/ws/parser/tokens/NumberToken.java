package net.cabezudo.sofia.core.ws.parser.tokens;

public class NumberToken extends Token {

  private final int value;

  public NumberToken(String value) {
    this.value = Integer.parseInt(value);
  }

  @Override
  public boolean isNumber() {
    return true;
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }

  @Override
  public Integer toInteger() {
    return value;
  }

  @Override
  public Long toLong() {
    return Long.valueOf(value);
  }

  @Override
  public String getValue() {
    return toString();
  }
}
