package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.files.FileHelper;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.logger.Logger;

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
  private final CSSImports cssImports;
  private final Libraries libraries;
  private final Lines lines;

  CSSSourceFile(Site site, Path basePath, Path partialPath, TemplateVariables templateVariables, Caller caller) {
    this.site = site;
    this.basePath = basePath;
    this.partialPath = partialPath;
    this.templateVariables = templateVariables;
    this.caller = caller;
    this.cssImports = new CSSImports();
    this.libraries = new Libraries();
    this.lines = new CSSLines();
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
  public void add(CSSImport cssImport) {
    cssImports.add(cssImport);
  }

  @Override
  public void add(CSSImports newCSSImports) {
    for (CSSImport cssImport : newCSSImports) {
      cssImports.add(cssImport);
    }
  }

  @Override
  public void add(Line line) {
    if (line.isCSSImport()) {
      cssImports.add(new CSSImport(line));
    } else {
      lines.add(line);
    }
  }

  @Override
  public void add(Lines lines) {
    for (Line line : lines) {
      this.lines.add(line);
    }
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
    Logger.debug("Creating the css file %s.", filePath);
    StringBuilder code = new StringBuilder();

    for (Library library : libraries) {
      library.getCascadingStyleSheetFiles().forEach(file -> cssImports.add(file.getCascadingStyleSheetImports()));
    }
    code.append(cssImports.toString());

    for (Library library : libraries) {
      Logger.debug("Search files in library %s.", library);
      library.getCascadingStyleSheetFiles().stream().map(file -> {
        Logger.debug("Add lines from file %s.", file.getPartialPath());
        return file;
      }).forEachOrdered(file -> {
        code.append(file.getCascadingStyleSheetLines().getCode()).append('\n');
      });
    }
    code.append(lines.getCode());

    Path parentFile = filePath.getParent();
    if (!Files.exists(parentFile)) {
      Files.createDirectories(parentFile);
    }
    Files.write(filePath, code.toString().getBytes(Configuration.getInstance().getEncoding()));
  }

  @Override
  public CSSImports getCascadingStyleSheetImports() {
    return cssImports;
  }

  @Override
  public Lines getCascadingStyleSheetLines() {
    return lines;
  }

  @Override
  public boolean searchHTMLTag(SofiaSource actual, String line,
          Path filePath, int lineNumber) throws InvalidFragmentTag {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Lines getJavaScriptLines() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  void load(Path fileBasePath, String partialFileName, Caller newCaller) throws IOException, LocatedSiteCreationException {
    Path defaultBasePath = getBasePath();
    Path cssFullSourceFilePath = FileHelper.resolveFullFilePath(fileBasePath, defaultBasePath, partialFileName, newCaller);

    if (!Files.exists(cssFullSourceFilePath)) {
      Logger.debug("[CSSSourceFile:load] File %s NOT FOUND.", partialFileName);
      return;
    } else {
      Logger.debug("[CSSSourceFile:load] Cascading Style Sheet file %s FOUND.", partialFileName);
    }

    add(new CodeLine("/* " + partialFileName + " addeded by " + newCaller + " */"));
    List<String> linesFromFile = Files.readAllLines(cssFullSourceFilePath);
    int lineNumber = 1;
    Logger.debug("[CSSSourceFile:load] Replace template variables on source file %s.", cssFullSourceFilePath);
    for (String line : linesFromFile) {
      try {
        String newLine = getTemplateVariables().replace(line);
        add(new CodeLine(newLine, lineNumber));
      } catch (UndefinedLiteralException e) {
        Position position = new Position(lineNumber, e.getRow());
        throw new LocatedSiteCreationException(e.getMessage(), Paths.get(partialFileName), position);
      }
      lineNumber++;
    }
  }

  StringBuilder readFileFor(Path filePath) throws IOException {
    Path htmlPartialPath = site.getVersionPath().relativize(filePath);
    Path cssBasePath = site.getFilesPath(htmlPartialPath);
    String htmlFileName = filePath.getFileName().toString();
    String cssFileName = FileHelper.removeExtension(htmlFileName) + ".css";
    Path cssFilePath = cssBasePath.resolve(cssFileName);
    if (!Files.exists(cssFilePath)) {
      Logger.debug("The Cascading Style Sheet source file %s DO NOT exists.", cssFilePath);
      return null;
    }
    StringBuilder sb = new StringBuilder((int) Files.size(cssFilePath));
    List<String> linesFromFile = Files.readAllLines(cssFilePath);
    linesFromFile.forEach(line -> sb.append(line).append('\n'));
    return sb;
  }

  @Override
  public String toString() {
    return lines.toString();
  }
}
