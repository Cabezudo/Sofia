package net.cabezudo.sofia.core.creator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import net.cabezudo.json.JSON;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.html.HTMLTagFactory;
import net.cabezudo.sofia.core.html.Tag;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.users.profiles.ProfileManager;
import net.cabezudo.sofia.core.users.profiles.Profiles;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.12.03
 */
class HTMLSourceFile implements SofiaSource {

  private final Site site;
  private final Path basePath;
  private final Path partialPath;
  private final Path partialFilePath;
  private final TemplateVariables templateVariables;
  private final Caller caller;
  private final Lines lines;
  protected final Libraries libraries;
  private final CSSSourceFile css;
  private final JSSourceFile js;
  private Profiles profiles = new Profiles();

  HTMLSourceFile(Site site, Path basePath, Path partialPath, TemplateVariables templateVariables, Caller caller) throws IOException, LocatedSiteCreationException, SiteCreationException, SQLException, InvalidFragmentTag {
    this.site = site;
    this.basePath = basePath;
    this.partialFilePath = partialPath;
    this.partialPath = partialPath.getParent();
    this.templateVariables = templateVariables;
    this.caller = caller;
    this.lines = new Lines();
    this.libraries = new Libraries();

    String cssPartialName = getVoidPartialPathName() + ".css";

    Path cssPartialPath = Paths.get(cssPartialName);

    Path jsPartialPath = Paths.get(getVoidPartialPathName() + ".js");
    css = new CSSSourceFile(site, basePath, cssPartialPath, templateVariables, caller);
    if (cssPartialPath.toString().equals("manager/sites/../header.css")) {
      throw new RuntimeException();
    }
    css.load(cssPartialName, caller);
    js = new JSSourceFile(site, basePath, jsPartialPath, templateVariables, caller);
    js.loadFile();
  }

  Site getSite() {
    return site;
  }

  Path getBasePath() {
    return basePath;
  }

  Path getPartialFilePath() {
    return partialFilePath;
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

  void loadJSONConfigurationFile() throws IOException, LocatedSiteCreationException, SiteCreationException, SQLException, InvalidFragmentTag, LibraryVersionConflictException {
    Path jsonPartialPath = Paths.get(getVoidPartialPathName() + ".json");
    Path jsonSourceFilePath = getBasePath().resolve(jsonPartialPath);
    Logger.debug("Search configuration file %s for HTML source file %s.", jsonPartialPath, getPartialFilePath());
    if (Files.isRegularFile(jsonSourceFilePath)) {
      Logger.debug("FOUND configuration file %s for HTML %s.", jsonPartialPath, getPartialFilePath());
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
        throw new SiteCreationException("Can't parse " + jsonSourceFilePath + ". " + e.getMessage());
      }
      String templateName = jsonObject.getNullString("template");
      if (templateName != null) {
        // Read template from template files
        Path commonsComponentsTemplatePath = Configuration.getInstance().getCommonsComponentsTemplatesPath();
        Path voidTemplatePath = Paths.get(templateName + ".html");

        Logger.debug("Load template %s from file %s in HTML source file.", voidTemplatePath, jsonPartialPath);
        jsonObject.remove("template");
        getTemplateVariables().merge(jsonObject);

        HTMLSourceFile templateFile = new HTMLSourceFile(getSite(), commonsComponentsTemplatePath, voidTemplatePath, getTemplateVariables(), null);
        templateFile.loadJSONConfigurationFile();
        templateFile.loadHTMLFile();
        profiles.add(templateFile.getProfiles());
        libraries.add(templateFile.getLibraries());
        this.lines.add(templateFile.getLines());
      } else {
        getTemplateVariables().merge(jsonObject);
      }
    }
  }

  void loadHTMLFile() throws IOException, LocatedSiteCreationException, SQLException, InvalidFragmentTag, SiteCreationException, LibraryVersionConflictException {
    SofiaSource actual = this;

    Path htmlSourceFilePath;
    if (caller == null) {
      htmlSourceFilePath = getBasePath().resolve(partialFilePath);
    } else {
      if (partialFilePath.startsWith("/")) {
        htmlSourceFilePath = getBasePath().resolve(partialFilePath);
      } else {
        Path parent = caller.getFilePath().getParent();
        htmlSourceFilePath = parent.resolve(partialFilePath);
      }
    }
    Logger.debug("Load HTML source file %s.", partialFilePath);

    if (!Files.exists(htmlSourceFilePath)) {
      throw new SiteCreationException("HTML file source " + getPartialFilePath() + " NOT FOUND.");
    }
    List<String> linesFromFile = Files.readAllLines(htmlSourceFilePath);
    int lineNumber = 1;
    for (String l : linesFromFile) {
      try {
        String newLine = getTemplateVariables().replace(l, lineNumber, htmlSourceFilePath);
        String trimmedNewLine = newLine.trim();

        do {
          if (searchHTMLTag(actual, trimmedNewLine, lineNumber)) {
            actual = this;
            break;
          }
          if (trimmedNewLine.startsWith("<script lib=\"") && trimmedNewLine.endsWith("\"></script>")) {
            String libraryReference = trimmedNewLine.substring(13, trimmedNewLine.length() - 11);
            Logger.debug("Library reference name found: %s.", libraryReference);

            Caller newCaller = new Caller(getBasePath(), getPartialFilePath(), lineNumber, getCaller());
            Library library = new Library(getSite(), libraryReference, getTemplateVariables(), newCaller);
            libraries.add(library);
            break;
          }
          if (trimmedNewLine.startsWith("<style file=\"") && trimmedNewLine.endsWith("\"></style>")) {
            String styleFilePartialFileName = trimmedNewLine.substring(13, trimmedNewLine.length() - 10);
            Logger.debug("Found independent style file call: %s.", styleFilePartialFileName);
            Caller newCaller = new Caller(getBasePath(), getPartialFilePath(), lineNumber, getCaller());
            css.load(styleFilePartialFileName, newCaller);
            break;
          }

          switch (trimmedNewLine) {
            case "<style>":
              actual = css;
              if (getCaller() == null) {
                actual.add(new CodeLine("/* created by system using content from " + getPartialFilePath() + ":" + lineNumber + " */", lineNumber));
              } else {
                actual.add(new CodeLine("/* created by system using " + getPartialFilePath() + ":" + lineNumber + " called from " + getCaller() + " */", lineNumber));
              }
              break;
            case "<script>":
              actual = js;
              if (getCaller() == null) {
                actual.add(new CodeLine("// created by system using " + getPartialFilePath() + ":" + lineNumber + ".", lineNumber));
              } else {
                actual.add(new CodeLine("// created by system using " + getPartialFilePath() + ":" + lineNumber + " called from " + getCaller(), lineNumber));
              }
              break;
            case "</html>":
              if (getCaller() == null) {
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
                Line processedLine = getProcessedLine(newLine, lineNumber);
                if (processedLine != null) {
                  actual.add(processedLine);
                  libraries.add(processedLine.getLibraries());
                }
                break;
              }
              actual.add(new CodeLine(newLine, lineNumber));
              break;
          }
        } while (false);
      } catch (UndefinedLiteralException e) {
        Position position = new Position(lineNumber, e.getRow());
        throw new LocatedSiteCreationException(e.getMessage(), getPartialFilePath(), position);
      }
      lineNumber++;
    }
  }

  private Line getProcessedLine(String line, int lineNumber) throws IOException, SiteCreationException, LocatedSiteCreationException, SQLException, InvalidFragmentTag, LibraryVersionConflictException {
    Tag tag = HTMLTagFactory.get(line);
    if (tag != null && tag.isSection()) {
      if (tag.getValue("file") != null) {
        HTMLFragmentLine fragmentLine = new HTMLFragmentLine(getSite(), getBasePath(), getPartialFilePath(), getTemplateVariables(), tag, lineNumber, getCaller());
        return fragmentLine;
      }
      if (tag.getValue("template") != null) {
        String id = tag.getId();
        if (id == null) {
          throw new LocatedSiteCreationException("A template call must have an id", getPartialFilePath(), new Position(lineNumber, 0));
        }
        Path basePath;
        String configurationFile = null;

        String configurationAttribute = tag.getValue("configuration");
        if (configurationAttribute == null) {
          if (getPartialPath() == null) {
            configurationFile = id + ".json";
          } else {
            configurationFile = getPartialPath().resolve(id + ".json").toString();
          }
        } else {
          configurationFile = configurationAttribute;
        }
        if (configurationFile.startsWith("/")) {
          configurationFile = configurationFile.substring(1);
          basePath = site.getSourcesPath();
        } else {
          basePath = getBasePath();
        }
        Logger.info("Load configuration file %s used for template with id %s.", configurationFile, id);
        try {
          getTemplateVariables().add(basePath, configurationFile, id);
        } catch (FileNotFoundException | JSONParseException | UndefinedLiteralException e) {
          throw new SiteCreationException(e.getMessage());
        }

        HTMLTemplateLine templateLine = new HTMLTemplateLine(getSite(), getBasePath(), getPartialFilePath(), getTemplateVariables(), tag, lineNumber, getCaller());
        return templateLine;
      }
    }

    return new CodeLine(line, lineNumber);
  }

  @Override
  public boolean searchHTMLTag(SofiaSource actual, String line,
          int lineNumber) throws SQLException, InvalidFragmentTag {
    if (line.startsWith("<html")) {
      if (getCaller() != null) {
        throw new InvalidFragmentTag("A HTML fragment can't have the <html> tag", 0);
      }
      int i = line.indexOf("profiles");
      if (i > 0) {
        String profileString = line.substring(i + 10, line.length() - 2);
        Logger.debug("Profiles: " + profileString);
        String[] ps = profileString.split(",");
        profiles = ProfileManager.getInstance().createFromNames(ps, getSite());
      }
      add(new CodeLine("<html>\n", lineNumber));
      return true;
    }
    return false;
  }

  @Override
  public Lines getLines() {
    return lines;
  }

  String getCode() {
    return lines.getCode();
  }

  void save(Path filePath) throws IOException {
    Logger.debug("Creating the html file %s.", filePath);
    StringBuilder code = new StringBuilder();
    for (Line line : lines) {
      code.append(line.getCode()).append('\n');
    }
    Files.write(filePath, code.toString().getBytes(Configuration.getInstance().getEncoding()));
  }

  Libraries getLibraries() {
    return libraries;
  }

  @Override
  public void add(Line line
  ) {
    if (line == null) {
      return;
    }
    lines.add(line);
  }

  @Override
  public void add(Lines lines
  ) {
    lines.add(lines);
  }

  @Override
  public final String getVoidPartialPathName() {
    String partialPathName = getPartialFilePath().toString();
    return partialPathName.substring(0, partialPathName.length() - 5);
  }

  Profiles getProfiles() {
    return profiles;
  }

  @Override
  public Lines getJavaScriptLines() {
    Lines codeLines = new Lines();
    codeLines.add(js.getJavaScriptLines());
    for (Line line : this.lines) {
      codeLines.add(line.getJavaScriptLines());
    }
    return codeLines;
  }

  @Override
  public Lines getCascadingStyleSheetLines() {
    Lines codeLines = new Lines();
    codeLines.add(css.getCascadingStyleSheetLines());
    for (Line line : this.lines) {
      codeLines.add(line.getCascadingStyleSheetLines());
    }
    return codeLines;
  }
}
