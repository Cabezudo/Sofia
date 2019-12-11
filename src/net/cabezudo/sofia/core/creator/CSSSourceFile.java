package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.12.03
 */
class CSSSourceFile extends SofiaSourceFile {

  private final Libraries libraries;
  private final Lines lines;

  CSSSourceFile(Site site, Path basePath, Path partialPath, TemplateVariables templateVariables, Caller caller) {
    super(site, basePath, partialPath, templateVariables, caller);
    this.libraries = new Libraries();
    this.lines = new Lines();
  }

  void loadFile() throws IOException, LocatedSiteCreationException {
    Path cssSourceFilePath = getBasePath().resolve(getPartialPath());
    Logger.debug("Load Cascading Style Sheet source file %s.", getPartialPath());

    if (!Files.exists(cssSourceFilePath)) {
      Logger.debug("File %s NOT FOUND.", getPartialPath());
      return;
    }

    add(new CodeLine(cssSourceFilePath + " addeded by " + getCaller() + "."));
    List<String> linesFromFile = Files.readAllLines(cssSourceFilePath);
    int lineNumber = 1;
    for (String line : linesFromFile) {
      try {
        String newLine = getTemplateVariables().replace(line, lineNumber, cssSourceFilePath);
        add(new CodeLine(newLine, lineNumber));
      } catch (UndefinedLiteralException e) {
        Position position = new Position(lineNumber, e.getRow());
        throw new LocatedSiteCreationException(e.getMessage(), getPartialPath(), position);
      }
      lineNumber++;
    }
  }

  Lines getLines() {
    return lines;
  }

  @Override
  public void add(Line line) {
    lines.add(line);
  }

  @Override
  public void add(Lines lines) {
    this.lines.add(lines);
  }

  public void add(Libraries libraries) throws LibraryVersionConflictException {
    this.libraries.add(libraries);
  }

  @Override
  public String getVoidPartialPathName() {
    String partialPathName = getPartialPath().toString();
    return partialPathName.substring(0, partialPathName.length() - 4);
  }

  void save(Path filePath) throws IOException {
    Logger.debug("Creating the js file %s.", filePath);
    StringBuilder code = new StringBuilder();

    for (Library library : libraries) {
      Logger.debug("Search files in library %s.", library);
      for (CSSSourceFile file : library.getCascadingStyleSheetFiles()) {
        code.append("/* Library ").append(library.toString()).append(" addeded by system. */\n");
        Logger.debug("Add lines from file %s.", file.getPartialPath());
        code.append(file.getCascadingStyleSheetCode()).append('\n');
      }
    }

    code.append(lines.getCode());
    Files.write(filePath, code.toString().getBytes(Configuration.getInstance().getEncoding()));
  }

  String getCascadingStyleSheetCode() {
    return lines.getCode();
  }

  Lines getCascadingStyleSheetLines() {
    return lines;
  }
}