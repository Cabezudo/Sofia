package net.cabezudo.sofia.core.ws.parser.tokens;

import net.cabezudo.sofia.core.InvalidPathParameterException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.15
 */
public abstract class WSToken {

  @Override
  public abstract String toString();

  public abstract Integer toInteger() throws InvalidPathParameterException;

  public abstract Long toLong() throws InvalidPathParameterException;

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

  public boolean match(WSToken t) {
    return this.isVariable() || t.isVariable() || this.toString().equals(t.toString());
  }
}
