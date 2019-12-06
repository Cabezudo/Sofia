package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.25
 */
class JavaScriptSourceFile extends AbstractSofiaSourceFile {

  private final Lines lines;

  public JavaScriptSourceFile(Path basePath, Path partialPath, TemplateVariables templateVariables, Caller caller) throws IOException {
    super(basePath, partialPath, templateVariables, caller);
    this.lines = new Lines();
  }

  @Override
  void add(String l, int lineNumber) {
    Line line = new CodeLine(l, lineNumber);
    lines.add(line);
  }

  Lines getLines() {
    return lines;
  }

}
