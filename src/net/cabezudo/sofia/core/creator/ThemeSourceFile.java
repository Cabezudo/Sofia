package net.cabezudo.sofia.core.creator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.26
 */
final class ThemeSourceFile extends SofiaSourceFile {

  private final String themeName;
  private final Lines lines;

  ThemeSourceFile(Site site, String themeName, TemplateVariables templateVariables) throws IOException, SiteCreationException {
    super(site, Configuration.getInstance().getCommonsThemesPath(), Paths.get(themeName), templateVariables, null);
    this.themeName = themeName;
    this.lines = new Lines();
  }

  void loadFile() throws IOException, SiteCreationException {
    Path partialThemeBasePath = Paths.get(themeName);
    Path themeBasePath = Configuration.getInstance().getCommonsThemesPath();
    Path templateVariablesBasePath = themeBasePath.resolve(partialThemeBasePath);
    Path partialStyleFilePath = partialThemeBasePath.resolve("style.css");
    Path styleFilePath = themeBasePath.resolve(partialStyleFilePath);

    try {
      getTemplateVariables().add(templateVariablesBasePath, "values.json");
    } catch (UndefinedLiteralException | JSONParseException | FileNotFoundException e) {
      throw new SiteCreationException(e.getMessage());
    }

    Logger.debug("Load style.css theme source file %s.", getPartialPath());
    add(new CodeLine("/* Addeded by system using content from " + partialStyleFilePath + " */"));

    List<String> linesFromFile = Files.readAllLines(styleFilePath);
    int lineNumber = 1;
    for (String line : linesFromFile) {
      try {
        String newLine = getTemplateVariables().replace(line, lineNumber, partialStyleFilePath);
        add(new CodeLine(newLine, lineNumber));
      } catch (UndefinedLiteralException e) {
        Position position = new Position(lineNumber, e.getRow());
        throw new LocatedSiteCreationException(e.getMessage(), partialStyleFilePath, position);
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
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public String getVoidPartialPathName() {
    throw new UnsupportedOperationException("Not supported.");
  }

  Lines getCascadingStyleSheetLines() {
    return lines;
  }
}
