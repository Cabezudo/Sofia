package net.cabezudo.sofia.core.http.url.parser.tokens;

public class NumberToken extends URLToken {

  private final String stringValue;
  private final int intValue;

  public NumberToken(String value) {
    this.stringValue = value;
    this.intValue = Integer.parseInt(value);
  }

  @Override
  public boolean isNumber() {
    return true;
  }

  @Override
  public String toString() {
    return stringValue;
  }

  @Override
  public Integer toInteger() {
    return intValue;
  }

  @Override
  public Long toLong() {
    return Long.valueOf(intValue);
  }
}