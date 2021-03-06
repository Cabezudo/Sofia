package net.cabezudo.sofia.core.creator;

import net.cabezudo.json.exceptions.PropertyNotExistException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
public class CodeLine extends Line {

  private final String line;

  public CodeLine(String line, int lineNumber) {
    super(lineNumber);
    this.line = line;
  }

  private CodeLine(StringBuilder sb, int lineNumber) {
    this(sb.toString(), lineNumber);
  }

  CodeLine(String line) {
    this(line, 0);
  }

  @Override
  Line replace(TemplateVariables templateVariables) throws UndefinedLiteralException {
    StringBuilder sb = new StringBuilder();
    int i;
    int last = 0;

    while ((i = line.indexOf("#{", last)) != -1) {
      sb.append(line.substring(last, i));
      last = line.indexOf("}", i);
      String name = line.substring(i + 2, last);
      last++;
      String value;
      try {
        value = templateVariables.digString(name);
      } catch (PropertyNotExistException e) {
        throw new UndefinedLiteralException(name, i + 3, e);
      }
      sb.append(value);
    }
    sb.append(line.substring(last));
    return new CodeLine(sb, super.getLineNumber());
  }

  @Override
  String getCode() {
    return line;
  }

  @Override
  boolean isEmpty() {
    return line.trim().isEmpty();
  }

  @Override
  boolean isNotEmpty() {
    return !line.trim().isEmpty();
  }

  @Override
  boolean isCSSImport() {
    return line.trim().startsWith("@import ");
  }

  @Override
  public int compareTo(Line o) {
    CodeLine codeLine = (CodeLine) o;
    int c = line.compareTo(codeLine.line);
    if (c != 0) {
      return c;
    }
    return super.getLineNumber() - codeLine.getLineNumber();
  }

  @Override
  boolean startWith(String start) {
    return line.trim().startsWith(start);
  }

  @Override
  boolean endWith(String end) {
    return line.trim().endsWith(end);
  }

  @Override
  Libraries getLibraries() {
    return null;
  }

  @Override
  Lines getJavaScriptLines() {
    return null;
  }

  @Override
  CSSImports getCascadingStyleSheetImports() {
    return new CSSImports();
  }

  @Override
  Lines getCascadingStyleSheetLines() {
    return new CSSLines();
  }

  @Override
  public String toString() {
    return this.getCode();
  }
}
