package net.cabezudo.sofia.core.ws.parser.tokens;

public class StringToken extends WSToken {

  private final String value;

  public StringToken(String value) {
    this.value = value;
  }

  @Override
  public boolean isString() {
    return true;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public Integer toInteger() {
    return Integer.parseInt(value);
  }

  @Override
  public Long toLong() {
    return Long.parseLong(value);
  }
}
