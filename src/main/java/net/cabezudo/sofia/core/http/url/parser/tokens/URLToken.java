package net.cabezudo.sofia.core.http.url.parser.tokens;

import java.math.BigDecimal;
import net.cabezudo.sofia.core.InvalidPathParameterException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.15
 */
public abstract class URLToken {

  @Override
  public abstract String toString();

  public abstract Integer toInteger() throws InvalidPathParameterException;

  public abstract BigDecimal toBigDecimal() throws InvalidPathParameterException;

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

  public boolean match(URLToken t) {
    return this.isVariable() || t.isVariable() || this.toString().equals(t.toString());
  }

}
