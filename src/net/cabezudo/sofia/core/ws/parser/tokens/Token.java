package net.cabezudo.sofia.core.ws.parser.tokens;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.15
 */
public abstract class Token {

  public abstract Integer toInteger();

  public abstract Long toLong();

  @Override
  public abstract String toString();

  public abstract String getValue();

  public boolean isPathSeparator() {
    return false;
  }

  public boolean isString() {
    return false;
  }

  public boolean isVariable() {
    return false;
  }

  public boolean isNumber() {
    return false;
  }

  public boolean match(Token t) {
    return this.isVariable() || t.isVariable() || this.getValue().equals(t.getValue());
  }
}
