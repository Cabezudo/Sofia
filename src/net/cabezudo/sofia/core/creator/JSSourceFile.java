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
class JSSourceFile extends SofiaSourceFile {

  private final Libraries libraries;
  private final Lines lines;

  JSSourceFile(Site site, Path basePath, Path partialPath, TemplateVariables templateVariables, Caller caller) {
    super(site, basePath, partialPath, templateVariables, caller);
    this.lines = new Lines();
    this.libraries = new Libraries();
  }

  void loadFile() throws IOException, LocatedSiteCreationException {
    Path jsSourceFilePath = getBasePath().resolve(getPartialPath());
    Logger.debug("Load JavaScript source file %s.", getPartialPath());

    List<String> linesFromFile = Files.readAllLines(jsSourceFilePath);
    int lineNumber = 1;
    for (String line : linesFromFile) {
      try {
        String newLine = getTemplateVariables().replace(line, lineNumber, jsSourceFilePath);
        add(new CodeLine(newLine, lineNumber));
      } catch (UndefinedLiteralException e) {
        Position position = new Position(lineNumber, e.getRow());
        throw new LocatedSiteCreationException(e.getMessage(), getPartialPath(), position);
      }
      lineNumber++;
    }
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

  void save(Path filePath) throws IOException {
    Logger.debug("Creating the js file %s.", filePath);
    StringBuilder code = new StringBuilder();

    for (Library library : libraries) {
      Logger.debug("Search files in library %s.", library);
      for (JSSourceFile file : library.getJSFiles()) {
        code.append("// Library ").append(library.toString()).append(" addeded by system.\n");
        Logger.debug("Add lines from file %s.", file.getPartialPath());
        code.append(file.getJavaScriptCode()).append('\n');
      }
    }
    code.append(lines.getCode());
    Files.write(filePath, code.toString().getBytes(Configuration.getInstance().getEncoding()));
  }

  @Override
  public String getVoidPartialPathName() {
    String partialPathName = getPartialPath().toString();
    return partialPathName.substring(0, partialPathName.length() - 3);
  }

  String getJavaScriptCode() {
    return lines.getCode();
  }

  Lines getJavaScriptLines() {
    return lines;
  }
}
