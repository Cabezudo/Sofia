package net.cabezudo.sofia.core.sic.tokens;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class EqualToken extends Token {

  public EqualToken(Position position) {
    super("=", position);
  }

  @Override
  public boolean isEqual() {
    return true;
  }
}
