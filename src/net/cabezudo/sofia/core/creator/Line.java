package net.cabezudo.sofia.core.creator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
public abstract class Line implements Comparable<Line> {

  private final int lineNumber;

  public Line(int lineNumber) {
    this.lineNumber = lineNumber;
  }

  int getLineNumber() {
    return lineNumber;
  }

  abstract Line replace(TemplateLiterals templateLiterals) throws UndefinedLiteralException;

  abstract boolean isNotEmpty();
}
