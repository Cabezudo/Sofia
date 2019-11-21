package net.cabezudo.sofia.core.creator;

import java.nio.file.Path;
import net.cabezudo.json.exceptions.PropertyNotExistException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
public class CodeLine extends Line {

  private final String line;
  private final Path partialFilePath;

  public CodeLine(String line, Path partialFilePath, int lineNumber) {
    super(lineNumber);
    this.partialFilePath = partialFilePath;
    this.line = line;
  }

  private CodeLine(StringBuilder sb, Path partialFilePath, int lineNumber) {
    this(sb.toString(), partialFilePath, lineNumber);
  }

  @Override
  Line replace(TemplateLiterals templateLiterals) throws UndefinedLiteralException {
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
        value = templateLiterals.digString(name);
      } catch (PropertyNotExistException e) {
        Position position = new Position(super.getLineNumber(), i + 3);
        throw new UndefinedLiteralException(name, partialFilePath, position, e);
      }
      sb.append(value);
    }
    sb.append(line.substring(last));
    return new CodeLine(sb, partialFilePath, super.getLineNumber());
  }

  @Override
  public String toString() {
    return line;
  }

  @Override
  boolean isNotEmpty() {
    return !line.isEmpty();
  }

  @Override
  public int compareTo(Line o) {
    CodeLine codeLine = (CodeLine) o;
    int c = partialFilePath.compareTo(codeLine.partialFilePath);
    if (c != 0) {
      return c;
    }
    c = line.compareTo(codeLine.line);
    if (c != 0) {
      return c;
    }
    return super.getLineNumber() - codeLine.getLineNumber();
  }
}
