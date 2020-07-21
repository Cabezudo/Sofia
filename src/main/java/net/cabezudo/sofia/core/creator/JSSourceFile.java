package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.12.03
 */
class JSSourceFile implements SofiaSource {

  private final Site site;
  private final Path basePath;
  private final Path partialPath;
  private final TemplateVariables templateVariables;
  private final Caller caller;
  private final CSSImports imports;
  private final Libraries libraries;
  private final Lines lines;

  JSSourceFile(Site site, Path basePath, Path partialPath, TemplateVariables templateVariables, Caller caller) {
    this.site = site;
    this.basePath = basePath;
    this.partialPath = partialPath;
    this.templateVariables = templateVariables;
    this.caller = caller;
    this.imports = new CSSImports();
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

  void loadFile() throws IOException, LocatedSiteCreationException {
    Path jsSourceFilePath = getBasePath().resolve(getPartialPath());

    if (!Files.exists(jsSourceFilePath)) {
      Logger.debug("File %s NOT FOUND.", getPartialPath());
      return;
    } else {
      Logger.debug("JavaScript file %s FOUND.", getPartialPath());
    }

    List<String> linesFromFile = Files.readAllLines(jsSourceFilePath);
    int lineNumber = 1;
    for (String line : linesFromFile) {
      try {
        String newLine = getTemplateVariables().replace(line);
        add(new CodeLine(newLine, lineNumber));
      } catch (UndefinedLiteralException e) {
        Position position = new Position(lineNumber, e.getRow());
        throw new LocatedSiteCreationException(e.getMessage(), getPartialPath(), position);
      }
      lineNumber++;
    }
  }

  @Override
  public void add(CSSImport cssImport) {
    imports.add(cssImport);
  }

  @Override
  public void add(CSSImports newCSSImports) {
    for (CSSImport cssImport : newCSSImports) {
      imports.add(cssImport);
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

    String templateVariablesCode = "const templateVariables = " + getTemplateVariables().toJSON() + ";\n";
    code.append(templateVariablesCode);

    for (Library library : libraries) {
      Logger.debug("Search files in library %s.", library);
      library.getJavaScritpFiles().stream().map(file -> {
        code.append("// Library ").append(library.toString()).append(" addeded by system.\n");
        Logger.debug("Add lines from file %s.", file.getPartialPath());
        return file;
      }).forEachOrdered(file -> code.append(file.getJavaScriptCode()).append('\n'));
    }
    code.append(lines.getCode());

    Path parentFile = filePath.getParent();
    if (!Files.exists(parentFile)) {
      Files.createDirectories(parentFile);
    }
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

  @Override
  public Lines getJavaScriptLines() {
    return lines;
  }

  @Override
  public boolean searchHTMLTag(SofiaSource actual, String line, Path filePath, int lineNumber) throws SQLException, InvalidFragmentTag {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Lines getLines() {
    return lines;
  }

  @Override
  public CSSImports getCascadingStyleSheetImports() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Lines getCascadingStyleSheetLines() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
