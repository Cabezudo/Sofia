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

  abstract Line replace(TemplateVariables templateVariables) throws UndefinedLiteralException;

  abstract boolean isEmpty();

  abstract boolean isNotEmpty();

  abstract boolean startWith(String start);

  abstract boolean endWith(String end);

  abstract String getCode();

  abstract Libraries getLibraries();

  abstract Lines getJavaScriptLines();

  abstract CSSImports getCascadingStyleSheetImports();

  abstract Lines getCascadingStyleSheetLines();

  boolean isCSSImport() {
    return false;
  }

}
