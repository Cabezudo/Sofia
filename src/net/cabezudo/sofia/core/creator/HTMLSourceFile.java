package net.cabezudo.sofia.core.creator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.html.HTMLTagFactory;
import net.cabezudo.sofia.core.html.Tag;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.users.profiles.Profiles;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.12.03
 */
abstract class HTMLSourceFile implements SofiaSource {

  private final Site site;
  private final Path basePath;
  private final Path partialPath;
  private final Path partialFilePath;
  private final TemplateVariables templateVariables;
  private final Caller caller;
  private final Lines lines;
  protected final CSSImports cssImports;
  protected final Libraries libraries;
  private final CSSSourceFile css;
  private final JSSourceFile js;
  private Profiles profiles = new Profiles();
  private final String id;

  HTMLSourceFile(Site site, Path basePath, Path partialPath, String id, TemplateVariables templateVariables, Caller caller)
          throws IOException, LocatedSiteCreationException, SiteCreationException, SQLException, InvalidFragmentTag {
    this.site = site;
    this.basePath = basePath;
    this.partialFilePath = partialPath;
    this.partialPath = partialPath.getParent();
    this.templateVariables = templateVariables;
    this.caller = caller;
    this.lines = new Lines();
    this.cssImports = new CSSImports();
    this.libraries = new Libraries();
    this.id = id;

    String cssPartialFileName = getVoidPartialPathName() + ".css";
    Path cssPartialPath = Paths.get(cssPartialFileName);
    Path jsPartialPath = Paths.get(getVoidPartialPathName() + ".js");

    css = new CSSSourceFile(site, basePath, cssPartialPath, templateVariables, caller);
    css.load(basePath, cssPartialFileName, caller);
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

  abstract Path getSourceFilePath(Caller caller) throws SiteCreationException;

  void loadHTMLFile()
          throws IOException, LocatedSiteCreationException, SQLException, InvalidFragmentTag, SiteCreationException, LibraryVersionConflictException, JSONParseException {

    if (caller == null) {
      Logger.debug("[HTMLSourceFile:loadHTMLFile] Load HTML %s requested.", partialFilePath);
    } else {
      Logger.debug("[HTMLSourceFile:loadHTMLFile] Load HTML %s called from %s.", partialFilePath, caller);
    }
    SofiaSource actual = this;

    // Search for a configuration file using the name of the page
    JSONConfiguration jsonSourceConfiguration = new JSONConfiguration();
    JSONObject jsonConfiguration = jsonSourceConfiguration.load(this);
    Path jsonPartialPath = jsonSourceConfiguration.getJSONPartialPath();

    String templateReference = null;
    String pageReference = null;
    if (jsonConfiguration != null) {
      templateReference = jsonConfiguration.getNullString("template");
      jsonConfiguration.remove("template");

      pageReference = jsonConfiguration.getNullString("page");
      jsonConfiguration.remove("page");

      if (templateReference != null && pageReference != null) {
        throw new SiteCreationException("You can't set a template and a page at the same time.");
      }
    }

    Logger.debug("[HTMLSourceFile:loadHTMLFile] Search in %s for a reference to another file.", jsonPartialPath);
    do {
      // Search for template property and if exist read the template file
      if (templateReference != null) {
        Logger.debug("[HTMLSourceFile:loadHTMLFile] The configuration file has a template property. Load templte %s.", templateReference);
        Path commonsComponentsTemplatePath = Configuration.getInstance().getCommonsComponentsTemplatesPath();
        Path voidTemplatePath = Paths.get(templateReference + ".html");

        Logger.debug("[HTMLSourceFile:loadHTMLFile] Load template %s from file %s in HTML source file.", voidTemplatePath, jsonPartialPath);

        Caller templateCaller = new Caller(getBasePath(), getPartialFilePath(), 0, caller);
        HTMLSourceFile templateFile = new JSONTemplateHTMLSourceFile(getSite(), commonsComponentsTemplatePath, voidTemplatePath, getTemplateVariables(), templateCaller);
        templateFile.loadHTMLFile();
        profiles.add(templateFile.getProfiles());
        libraries.add(templateFile.getLibraries());
        css.add(templateFile.getCascadingStyleSheetImports());
        css.add(templateFile.getCascadingStyleSheet().getLines());
        js.add(templateFile.getJavaScript().getLines());
        this.lines.add(templateFile.getLines());
        break;
      }

      // Search for page property and if exist read the page file
      if (pageReference != null) {
        Logger.debug("[HTMLSourceFile:loadHTMLFile] The configuration file has a page property. Load page %s.", pageReference);
        Path commonsComponentsTemplatePath = Configuration.getInstance().getCommonsComponentsTemplatesPath();
        Path voidPagePath = Paths.get(pageReference + ".html");

        Logger.debug("[HTMLSourceFile:loadHTMLFile] Load page %s from file %s in HTML source file.", voidPagePath, jsonPartialPath);

        Caller pageCaller = new Caller(getBasePath(), getPartialFilePath(), 0, caller);
        HTMLSourceFile pageSourceFile = new HTMLPageSourceFile(getSite(), commonsComponentsTemplatePath, voidPagePath, getTemplateVariables(), pageCaller);
        pageSourceFile.loadHTMLFile();
        profiles.add(pageSourceFile.getProfiles());
        libraries.add(pageSourceFile.getLibraries());
        css.add(pageSourceFile.getCascadingStyleSheetImports());
        css.add(pageSourceFile.getCascadingStyleSheet().getLines());
        js.add(pageSourceFile.getJavaScript().getLines());
        this.lines.add(pageSourceFile.getLines());
        break;
      }
      Logger.debug("HTMLSourceFile:loadHTMLFile] No references to templates or pages found in configuration file %s. An attempt is made to read the html file %s.", jsonPartialPath, partialFilePath);

      final Path htmlSourceFilePath = getSourceFilePath(caller);

      Logger.debug("[HTMLSourceFile:loadHTMLFile] Full path to HTML file to load %s.", htmlSourceFilePath);
      List<String> linesFromFile = Files.readAllLines(htmlSourceFilePath);
      int lineNumber = 1;
      for (String line : linesFromFile) {
        String newLine = replaceTemplateVariables(line, lineNumber, htmlSourceFilePath);
        String trimmedNewLine = newLine.trim();

        do {
          if (searchHTMLTag(actual, trimmedNewLine, htmlSourceFilePath, lineNumber)) {
            actual = this;
            break;
          }

          // Search for HTML tags with JavaScript libraries references
          if (trimmedNewLine.startsWith("<script lib=\"") && trimmedNewLine.endsWith("\"></script>")) {
            String libraryReference = trimmedNewLine.substring(13, trimmedNewLine.length() - 11);
            Logger.debug("[HTMLSourceFile:loadHTMLFile] Library reference name found: %s.", libraryReference);

            Caller newCaller = new Caller(getBasePath(), getPartialFilePath(), lineNumber, getCaller());
            Library library = new Library(getSite(), libraryReference, getTemplateVariables(), newCaller);
            libraries.add(library);
            break;
          }

          // Search for HTML tags with CSS file references
          if (trimmedNewLine.startsWith("<style file=\"") && trimmedNewLine.endsWith("\"></style>")) {
            String styleFilePartialFileName = trimmedNewLine.substring(13, trimmedNewLine.length() - 10);
            Logger.debug("HTMLSourceFile:loadHTMLFile:Found independent style file call: %s.", styleFilePartialFileName);
            Caller newCaller = new Caller(getBasePath(), getPartialFilePath(), lineNumber, getCaller());
            css.load(null, styleFilePartialFileName, newCaller);
            break;
          }

          switch (trimmedNewLine) {
            // Search for embeded CSS code
            case "<style>":
              actual = css;
              if (getCaller() == null) {
                actual.add(new CodeLine("/* created by system using content from " + getPartialFilePath() + ":" + lineNumber + " */", lineNumber));
              } else {
                CodeLine codeLine = new CodeLine("/* created by system using " + getPartialFilePath() + ":" + lineNumber + " called from " + getCaller() + " */", lineNumber);
                actual.add(codeLine);
              }
              break;
            case "<script>":
            case "<script class=\"test\">":
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
                Line processedLine = getProcessedLine(htmlSourceFilePath, newLine, lineNumber);
                if (processedLine != null) {
                  actual.add(processedLine);
                  libraries.add(processedLine.getLibraries());
                }
                break;
              }
              CodeLine codeLine = new CodeLine(newLine, lineNumber);
              actual.add(codeLine);
              break;
          }
        } while (false);
        lineNumber++;
      }
    } while (false);
  }
//

  abstract String replaceTemplateVariables(String line, int lineNumber, Path htmlSourceFilePath) throws LocatedSiteCreationException;

  Line getProcessedLine(Path htmlSourceFilePath, String line, int lineNumber)
          throws IOException, SiteCreationException, LocatedSiteCreationException, SQLException, InvalidFragmentTag, LibraryVersionConflictException, JSONParseException {
    Tag tag = HTMLTagFactory.get(line);
    Path actualPath = htmlSourceFilePath.getParent();

    // If the tag is a section we search for a file or template in order to load the file
    if (tag != null && tag.isSection()) {
      if (tag.getValue("file") != null) {
        HTMLFragmentLine fragmentLine = new HTMLFragmentLine(getSite(), getBasePath(), getPartialFilePath(), getTemplateVariables(), tag, lineNumber, getCaller());
        fragmentLine.load();
        // TODO Add custom configuration for a file.
        return fragmentLine;
      }
      if (tag.getValue("template") != null) {
        String tagId = tag.getId();
        if (tagId == null) {
          throw new LocatedSiteCreationException("A template call must have an id", getPartialFilePath(), new Position(lineNumber, 0));
        }
        Path templateBasePath;
        String configurationFile;

        String configurationFileAttribute = tag.getValue("configurationFile");
        if (configurationFileAttribute == null) {
          if (getPartialPath() == null) {
            configurationFile = tagId + ".json";
          } else {
            configurationFile = getPartialPath().resolve(tagId + ".json").toString();
          }
        } else {
          configurationFile = configurationFileAttribute;
        }
        if (configurationFile.startsWith("/")) {
          configurationFile = configurationFile.substring(1);
          templateBasePath = site.getVersionedSourcesPath();
        } else {
          templateBasePath = actualPath;
        }
        Logger.info("Load configuration file %s used for template with id %s.", configurationFile, tagId);
        try {
          getTemplateVariables().add(templateBasePath, configurationFile, tagId);
        } catch (FileNotFoundException | JSONParseException | UndefinedLiteralException e) {
          throw new SiteCreationException(e.getMessage());
        }

        HTMLTemplateLine templateLine = new HTMLTemplateLine(getSite(), getBasePath(), getPartialFilePath(), getTemplateVariables(), tag, lineNumber, getCaller());
        templateLine.load();

        return templateLine;
      }
    }

    return new CodeLine(line, lineNumber);
  }

  @Override
  public abstract boolean searchHTMLTag(SofiaSource actual, String line, Path filePath, int lineNumber) throws SQLException, InvalidFragmentTag;

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
      String lineCode = line.getCode();
      code.append(lineCode).append('\n');
    }

    Files.write(filePath, code.toString().getBytes(Configuration.getInstance().getEncoding()));
  }

  Libraries getLibraries() {
    return libraries;
  }

  @Override
  public void add(CSSImport cssImport) {
    if (cssImport == null) {
      throw new RuntimeException("Null parameter");
    }
    lines.add(cssImport);
  }

  @Override
  public void add(CSSImports newCSSImports) {
    for (CSSImport cssImport : newCSSImports) {
      cssImports.add(cssImport);
    }
  }

  @Override
  public void add(Line line) {
    if (line == null) {
      return;
    }
    lines.add(line);
  }

  @Override
  public void add(Lines lines) {
    lines.add(lines);
  }

  @Override
  public final String getVoidPartialPathName() {
    String partialPathName = getPartialFilePath().toString();
    System.out.println("partialPathName: " + partialPathName);
    return partialPathName.substring(0, partialPathName.length() - 5);
  }

  void setProfiles(Profiles profiles) {
    this.profiles = profiles;
  }

  Profiles getProfiles() {
    return profiles;
  }

  private JSSourceFile getJavaScript() {
    return js;
  }

  @Override
  public Lines getJavaScriptLines() {
    Lines codeLines = new Lines();
    codeLines.add(js.getJavaScriptLines());
    for (Line line : getLines()) {
      codeLines.add(line.getJavaScriptLines());
    }
    return codeLines;
  }

  @Override
  public CSSImports getCascadingStyleSheetImports() {
    CSSImports imports = new CSSImports();
    imports.add(css.getCascadingStyleSheetImports());
    for (Line line : getLines()) {
      imports.add(line.getCascadingStyleSheetImports());
    }
    return imports;
  }

  private CSSSourceFile getCascadingStyleSheet() {
    return css;
  }

  @Override
  public Lines getCascadingStyleSheetLines() {
    Lines codeLines = new Lines();
    codeLines.add(css.getCascadingStyleSheetLines());
    for (Line line : getLines()) {
      Lines cssLines = line.getCascadingStyleSheetLines();
      codeLines.add(cssLines);
    }
    return codeLines;
  }

  String getId() {
    return id;
  }
}
