package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.25
 */
class HTMLSourceFile extends AbstractSofiaSourceFile {

  private final CascadingStyleSheetSourceFile css;
  private final JavaScriptSourceFile js;
  private Lines lines;

  HTMLSourceFile(Path basePath, Path partialPath, TemplateVariables templateVariables, Caller caller) throws IOException, SiteCreationException {
    super(basePath, partialPath, templateVariables, caller);
    lines = new Lines();

    css = new CascadingStyleSheetSourceFile(basePath, partialPath, templateVariables, caller);
    js = new JavaScriptSourceFile(basePath, partialPath, templateVariables, caller);
    AbstractSofiaSourceFile actual = this;

    Path sourceFilePath = basePath.resolve(partialPath);
    List<String> ls = Files.readAllLines(sourceFilePath);
    int lineNumber = 1;
    for (String l : ls) {
      try {
        String newLine = templateVariables.replace(l, lineNumber, sourceFilePath);

        switch (newLine) {
          case "<style>":
            actual = css;
            actual.add("/* created by system using " + partialPath + " called from " + caller + " */", lineNumber);
            break;
          case "<script>":
            actual = js;
            actual.add("//( created by system using " + partialPath + " called from " + caller, lineNumber);
            break;
          case "</html>":
            if (caller != null) {
              actual.add("</html>\n", lineNumber);
            }
            actual = this;
            break;
          case "</style>":
          case "</script>":
            actual = this;
            break;
          default:
            actual.add(newLine, lineNumber);
            break;
        }

      } catch (UndefinedLiteralException e) {
        Position position = new Position(lineNumber, e.getRow());
        throw new LocatedSiteCreationException(e.getMessage(), sourceFilePath, position);
      }
      lineNumber++;
    }
  }

  @Override
  void add(String l, int lineNumber) {
    Line line = new CodeLine(l, lineNumber);
    lines.add(line);
  }

  Lines getCSSLines() {
    return css.getLines();
  }

  Lines getJSLines() {
    return js.getLines();
  }

  Lines getHTMLLines() {
    return lines;
  }
}
