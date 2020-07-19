package net.cabezudo.sofia.core.creator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.06.06
 */
public class UndefinedLiteralException extends Exception {

  private final String literal;
  private final int row;

  UndefinedLiteralException(String literal, int row, Throwable cause) {
    super("Undefined literal: " + literal, cause);
    this.literal = literal;
    this.row = row;
  }

  int getRow() {
    return row;
  }

  String getUndefinedLiteral() {
    return literal;
  }
}
