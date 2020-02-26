package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.files.FileHelper;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.12.03
 */
class CSSSourceFile implements SofiaSource {

  private final Site site;
  private final Path basePath;
  private final Path partialPath;
  private final TemplateVariables templateVariables;
  private final Caller caller;
  private final Libraries libraries;
  private final Lines lines;

  CSSSourceFile(Site site, Path basePath, Path partialPath, TemplateVariables templateVariables, Caller caller) {
    this.site = site;
    this.basePath = basePath;
    this.partialPath = partialPath;
    this.templateVariables = templateVariables;
    this.caller = caller;
    this.libraries = new Libraries();
    this.lines = new Lines();
  }

  Site getSite() {
    return site;
  }

  Path getBasePath() {
    return basePath;
  }

  Path getPartialPath() {
    return partialPath;
  }

  TemplateVariables getTemplateVariables() {
    return templateVariables;
  }

  Caller getCaller() {
    return caller;
  }

  @Override
  public Lines getLines() {
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

  @Override
  public Lines getCascadingStyleSheetLines() {
    return lines;
  }

  @Override
  public boolean searchHTMLTag(SofiaSource actual, String line, int lineNumber) throws SQLException, InvalidFragmentTag {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Lines getJavaScriptLines() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  void load(String partialFileName, Caller newCaller) throws IOException, LocatedSiteCreationException {
    load(null, partialFileName, newCaller);
  }

  void load(Path loadFileBasePath, String partialFileName, Caller newCaller) throws IOException, LocatedSiteCreationException {
    Path cssFullSourceFilePath = FileHelper.resolveFullFilePath(loadFileBasePath, getBasePath(), partialFileName, newCaller);

    Logger.debug("Load Cascading Style Sheet source file %s.", partialFileName);

    if (!Files.exists(cssFullSourceFilePath)) {
      Logger.debug("File %s NOT FOUND.", getPartialPath());
      return;
    }

    add(new CodeLine("/* " + getPartialPath() + " addeded by " + getCaller() + " */"));
    List<String> linesFromFile = Files.readAllLines(cssFullSourceFilePath);
    int lineNumber = 1;
    for (String line : linesFromFile) {
      try {
        String newLine = getTemplateVariables().replace(line, lineNumber, cssFullSourceFilePath);
        add(new CodeLine(newLine, lineNumber));
      } catch (UndefinedLiteralException e) {
        Position position = new Position(lineNumber, e.getRow());
        throw new LocatedSiteCreationException(e.getMessage(), getPartialPath(), position);
      }
      lineNumber++;
    }
  }
}
