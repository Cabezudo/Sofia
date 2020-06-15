package net.cabezudo.sofia.core.sic.tokens;

public class Position {

  public static final Position INITIAL = new Position(1, 1);
  private final int row;
  public final int line;

  public Position(int line, int row) {
    this.line = line;
    this.row = row;
  }

  public int getLine() {
    return line;
  }

  public int getRow() {
    return row;
  }

  @Override
  public String toString() {
    return " Line " + getLine() + ", row " + getRow();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null) {
      return false;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    final Position other = (Position) object;
    if (this.row != other.row) {
      return false;
    }
    return this.line == other.line;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + this.row;
    hash = 67 * hash + this.line;
    return hash;
  }
}
