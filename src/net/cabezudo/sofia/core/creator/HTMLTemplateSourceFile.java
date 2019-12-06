package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import net.cabezudo.json.JSON;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.html.HTMLTagFactory;
import net.cabezudo.sofia.core.html.Tag;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.12.04
 */
class HTMLTemplateSourceFile extends HTMLSourceFile {

  private final CSSSourceFile css;
  private final JSSourceFile js;
  private final String id;

  HTMLTemplateSourceFile(Site site, Path basePath, Path partialPath, String id, TemplateVariables templateVariables, Caller caller, Libraries libraries) throws IOException, LocatedSiteCreationException, SiteCreationException, SQLException, InvalidFragmentTag {
    super(site, basePath, partialPath, templateVariables, caller, libraries);
    this.id = id;

    Path cssPartialPath = Paths.get(getVoidPartialPathName() + ".css");
    Path jsPartialPath = Paths.get(getVoidPartialPathName() + ".js");
    css = new CSSSourceFile(site, basePath, cssPartialPath, templateVariables, caller);
    js = new JSSourceFile(site, basePath, jsPartialPath, templateVariables, caller);
  }

  void loadJSONConfigurationFile() throws IOException, LocatedSiteCreationException, SiteCreationException, SQLException, InvalidFragmentTag {
    Path jsonPartialPath = Paths.get(getVoidPartialPathName() + ".json");
    Path jsonSourceFilePath = getBasePath().resolve(jsonPartialPath);
    if (Files.isRegularFile(jsonSourceFilePath)) {
      Logger.debug("FOUND template configuration file %s for %s.", jsonPartialPath, getPartialPath());
      List<String> jsonLines = Files.readAllLines(jsonSourceFilePath);
      StringBuilder sb = new StringBuilder();
      int lineNumber = 1;
      for (String line : jsonLines) {
        try {
          sb.append(getTemplateVariables().replace(line, lineNumber, jsonSourceFilePath));
        } catch (UndefinedLiteralException e) {
          Position position = new Position(lineNumber, e.getRow());
          throw new LocatedSiteCreationException(e.getMessage(), jsonPartialPath, position);
        }
        lineNumber++;
      }
      JSONObject jsonObject;
      try {
        jsonObject = JSON.parse(sb.toString()).toJSONObject();
      } catch (JSONParseException e) {
        throw new SiteCreationException("Cant parse " + jsonSourceFilePath + ". " + e.getMessage());
      }
      String templateName = jsonObject.getNullString("template");
      if (templateName != null) {
        // Read template from template files
        Path commonsComponentsTemplatePath = Configuration.getInstance().getCommonsComponentsTemplatesPath();
        Path templatePath = Paths.get(templateName);

        Logger.debug("Load template %s from file %s.", templatePath, jsonPartialPath);
        jsonObject.remove("template");
        HTMLSourceFile templateFile = new HTMLSourceFile(getSite(), commonsComponentsTemplatePath, templatePath, getTemplateVariables(), null, libraries);
        templateFile.loadJSONConfigurationFile();
        templateFile.loadHTMLFile();

        this.lines = templateFile.getLines();
      }
      JSONPair configurationPair = new JSONPair(id, jsonObject);
      JSONObject newJSONObject = new JSONObject();
      newJSONObject.add(configurationPair);
      getTemplateVariables().merge(newJSONObject);
    }
  }

  void loadHTMLFile() throws IOException, LocatedSiteCreationException, SQLException, InvalidFragmentTag, SiteCreationException {
    this.lines = new Lines();
    SofiaSourceFile actual = this;

    Path htmlSourceFilePath = getBasePath().resolve(getPartialPath());
    Logger.debug("Load template HTML source file %s.", getPartialPath());
    List<String> linesFromFile = Files.readAllLines(htmlSourceFilePath);
    int lineNumber = 1;
    for (String l : linesFromFile) {
      try {
        String newLine = getTemplateVariables().replace(id, l, lineNumber, htmlSourceFilePath);
        String trimmedNewLine = newLine.trim();

        do {
          actual = searchHTMLTag(actual, trimmedNewLine, lineNumber);
          if (trimmedNewLine.startsWith("<script lib=\"") && trimmedNewLine.endsWith("\"></script>")) {
            String libraryReference = trimmedNewLine.substring(13, trimmedNewLine.length() - 11);
            Logger.debug("Library reference name found: %s.", libraryReference);

            Caller newCaller = new Caller(getBasePath(), getPartialPath(), lineNumber);
            Library library = new Library(getSite(), libraryReference, getTemplateVariables(), newCaller);
            libraries.add(library);
            break;
          }

          switch (newLine) {
            case "<style>":
              actual = css;
              if (getCaller() == null) {
                actual.add(new CodeLine("/* created by system using content from " + getPartialPath() + ":" + lineNumber + " */", lineNumber));
              } else {
                actual.add(new CodeLine("/* created by system using " + getPartialPath() + ":" + lineNumber + " called from " + getCaller() + " */", lineNumber));
              }
              break;
            case "<script>":
              actual = js;
              if (getCaller() == null) {
                actual.add(new CodeLine("// created by system using " + getPartialPath() + ":" + lineNumber + ".", lineNumber));
              } else {
                actual.add(new CodeLine("// created by system using " + getPartialPath() + ":" + lineNumber + " called from " + getCaller(), lineNumber));
              }
              break;
            case "</html>":
              if (getCaller() != null) {
                actual.add(new CodeLine("</html>\n", lineNumber));
              }
              actual = this;
              break;
            case "</style>":
            case "</script>":
              actual = this;
              break;
            default:
              if (actual == this) {
                actual.add(getProcessedLine(newLine, lineNumber));
                break;
              }
              actual.add(new CodeLine(newLine, lineNumber));
              break;
          }
        } while (false);
      } catch (UndefinedLiteralException e) {
        Position position = new Position(lineNumber, e.getRow());
        throw new LocatedSiteCreationException(e.getMessage(), getPartialPath(), position);
      }
      lineNumber++;
    }
  }

  private Line getProcessedLine(String line, int lineNumber) throws IOException, SiteCreationException, LocatedSiteCreationException, SQLException, InvalidFragmentTag {
    StringBuilder sb = new StringBuilder();

    Tag tag = HTMLTagFactory.get(line);
    if (tag != null && tag.isSection()) {
      if (tag.getValue("file") != null) {
        HTMLFragmentLine fragmentLine = new HTMLFragmentLine(getSite(), getBasePath(), getPartialPath(), getTemplateVariables(), getLibraries(), tag, lineNumber);
        add(fragmentLine);
      }
      if (tag.getValue("template") != null) {
        if (tag.getId() == null) {
          throw new LocatedSiteCreationException("A template call must have an id", getPartialPath(), new Position(lineNumber, 0));
        }
        HTMLTemplateLine fragmentLine = new HTMLTemplateLine(getSite(), getBasePath(), getPartialPath(), getTemplateVariables(), getLibraries(), tag, lineNumber);
        add(fragmentLine);
      }
    }

    return new CodeLine(line, lineNumber);
  }

  @Override
  protected SofiaSourceFile searchHTMLTag(SofiaSourceFile actual, String line, int lineNumber) throws SQLException, InvalidFragmentTag {
    if (line.startsWith("<html")) {
      throw new InvalidFragmentTag("A HTML template can't have the <html> tag: " + getPartialPath(), 0);
    }
    return actual;
  }
}
