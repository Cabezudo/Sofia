package net.cabezudo.sofia.core.ws.parser.tokens;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.15
 */
public class PathSeparatorToken extends WSToken {

  @Override
  public boolean isPathSeparator() {
    return true;
  }

  @Override
  public String toString() {
    return "/";
  }

  @Override
  public Integer toInteger() {
    throw new ClassCastException("Can't convert a separator to integer");
  }

  @Override
  public Long toLong() {
    throw new ClassCastException("Can't convert a separator to long");
  }
}
