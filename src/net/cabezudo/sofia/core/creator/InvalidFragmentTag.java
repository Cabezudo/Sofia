package net.cabezudo.sofia.core.creator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.06.15
 */
public class InvalidFragmentTag extends Exception {

  private final int col;

  public InvalidFragmentTag(String message, int col) {
    super(message);
    this.col = col;
  }

  public int getRow() {
    return col;
  }
}
