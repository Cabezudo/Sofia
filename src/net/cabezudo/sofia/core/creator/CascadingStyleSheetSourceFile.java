package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.25
 */
class CascadingStyleSheetSourceFile extends AbstractSofiaSourceFile {

  private final Imports imports;
  private final Lines lines;

  CascadingStyleSheetSourceFile(Path basePath, String partialPathName, TemplateVariables templateVariables, Caller caller) throws IOException, SiteCreationException {
    this(basePath, Paths.get(partialPathName), templateVariables, caller);
  }

  CascadingStyleSheetSourceFile(Path basePath, Path partialPath, TemplateVariables templateVariables, Caller caller) throws IOException, SiteCreationException {
    super(basePath, partialPath, templateVariables, caller);
    this.imports = new Imports();
    this.lines = new Lines();
  }

  void add(Path basePath, Path partialPath, TemplateVariables templateVariables, Caller caller) throws IOException, LocatedSiteCreationException {
    Path sourceFilePath = basePath.resolve(partialPath);
    if (caller == null) {
      add(new CodeLine("/* " + partialPath + " addeded by system */"));
    } else {
      add(new CodeLine("/* " + partialPath + " addeded by " + caller + " */"));
    }
    if (Files.exists(sourceFilePath)) {
      List<String> ls = Files.readAllLines(sourceFilePath);
      int lineNumber = 1;
      for (String l : ls) {
        try {
          String newLine = templateVariables.replace(l, lineNumber, sourceFilePath);
          Line line = CSSFactory.get(newLine, lineNumber, caller);
          add(line);
        } catch (UndefinedLiteralException e) {
          Position position = new Position(lineNumber, e.getRow());
          throw new LocatedSiteCreationException(e.getMessage(), sourceFilePath, position);
        }
        lineNumber++;
      }
    } else {
      Logger.debug("%s file NOT FOUND.", sourceFilePath);
    }
  }

  void add(Path basePath, String partialPathName, TemplateVariables templateVariables, Caller caller) throws IOException, LocatedSiteCreationException {
    add(basePath, Paths.get(partialPathName), templateVariables, caller);
  }

  CascadingStyleSheetSourceFile(Path basePath, Path partialPath) throws IOException, SiteCreationException {
    this(basePath, partialPath, null, null);
  }

  @Override
  void add(String l, int lineNumber) {
    Line line = new CodeLine(l, lineNumber);
    add(line);
  }

  void save() throws SiteCreationException, IOException {
    Logger.debug("Saving file %s.", getFullTargetFilePath());
    StringBuilder code = new StringBuilder();
    for (Line line : imports) {
      code.append(line);
      code.append("\n");
    }
    for (Line line : lines) {
      code.append(line);
      code.append("\n");
    }
    Files.write(getFullTargetFilePath(), code.toString().getBytes(Configuration.getInstance().getEncoding()));
  }

  void add(ThemeSourceFile themeSourceFile) {
    for (CascadingStyleSheetSourceFile file : themeSourceFile.getFiles()) {
      imports.add(file.getImports());
      add(new CodeLine("/* Addeded from file " + file.getPartialPath() + " on theme " + themeSourceFile.getName() + " addeded by system */"));
      for (Line line : file.getLines()) {
        add(line);
      }
    }
  }

  void add(BaseHTMLSourceFile baseHTLMSourceFile) {
    add(new CodeLine("/* Addeded by system from " + baseHTLMSourceFile.getPartialPath() + " */"));
    for (Line line : baseHTLMSourceFile.getCSSLines()) {
      add(line);
    }
  }

  Imports getImports() {
    return imports;
  }

  Lines getLines() {
    return lines;
  }

  void add(Line line) {
    if (line.startWith("@import")) {
      imports.add(line);
      return;
    }
    if (line.isNotEmpty()) {
      lines.add(line);
    }
  }
}
