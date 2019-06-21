package net.cabezudo.sofia.core.creator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.06.06
 */
public class UndefinedLiteralException extends Exception {

  private final int line;
  private final int column;

  public UndefinedLiteralException(String message, Throwable cause, int line, int column) {
    super(message, cause);
    this.line = line;
    this.column = column;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }
}
