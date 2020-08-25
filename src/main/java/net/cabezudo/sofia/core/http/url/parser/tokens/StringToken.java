package net.cabezudo.sofia.core.http.url.parser.tokens;

public class StringToken extends URLToken {

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
