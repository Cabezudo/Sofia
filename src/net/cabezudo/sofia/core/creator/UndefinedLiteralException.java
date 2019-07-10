package net.cabezudo.sofia.core.creator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.06.06
 */
public class UndefinedLiteralException extends Exception {

  private final String literal;
  private final int line;
  private final int column;

  public UndefinedLiteralException(String literal, Throwable cause, int line, int column) {
    super("Undefined literal: " + literal, cause);
    this.literal = literal;
    this.line = line;
    this.column = column;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  public String getUndefinedLiteral() {
    return literal;
  }
}
