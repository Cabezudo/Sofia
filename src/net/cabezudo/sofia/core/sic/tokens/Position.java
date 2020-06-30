package net.cabezudo.sofia.core.sic.tokens;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;

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

  public JSONObject toJSON() {
    JSONObject jsonPosition = new JSONObject();
    JSONPair jsonLinePair = new JSONPair("line", line);
    jsonPosition.add(jsonLinePair);
    JSONPair jsonRowPair = new JSONPair("row", row);
    jsonPosition.add(jsonRowPair);
    return jsonPosition;
  }
}
