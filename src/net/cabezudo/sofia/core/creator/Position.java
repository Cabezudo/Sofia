package net.cabezudo.sofia.core.creator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
public class Position {

  private final int line;
  private final int row;

  Position(int line, int row) {
    this.line = line;
    this.row = row;
  }

  @Override
  public String toString() {
    return line + ":" + row;
  }
}
