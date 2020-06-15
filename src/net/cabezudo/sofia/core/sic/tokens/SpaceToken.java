package net.cabezudo.sofia.core.sic.tokens;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class SpaceToken extends Token {

  public SpaceToken(Position position) {
    super(" ", position);
  }

  @Override
  public boolean isSpace() {
    return true;
  }
}
