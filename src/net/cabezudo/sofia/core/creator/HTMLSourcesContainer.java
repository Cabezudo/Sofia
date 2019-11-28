package net.cabezudo.sofia.core.creator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;
import net.cabezudo.json.JSON;
import net.cabezudo.json.JSONPair;
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
 * @version 0.01.00, 2019.11.12
 */
public class HTMLSourcesContainer extends SofiaSourceContainer {

  private Profiles profiles;
  private SofiaSource actual;
  private final SofiaHTMLSource html;
  private final SofiaCascadingStyleSheetSource css;
  private final SofiaJavaScriptSource js;
  private final CascadingStyleSheetSourcesContainer cssContainer = new CascadingStyleSheetSourcesContainer();
  private final Libraries libraries = new Libraries();
  private final Site site;
  private final TemplateVariables templateVariables;

  HTMLSourcesContainer(Site site, TemplateVariables templateVariables) {
    this.site = site;
    this.templateVariables = templateVariables;
    this.css = new SofiaCascadingStyleSheetSource(null);
    this.js = new SofiaJavaScriptSource(null);
    this.html = new SofiaHTMLSource(null);
  }

  SofiaCascadingStyleSheetSource getSofiaCascadingStyleSheetSource() {
    return css;
  }

  SofiaJavaScriptSource getSofiaJavaScriptSource() {
    return js;
  }

  void load(Path basePath, Path voidPartialPath) throws IOException, SiteCreationException, LibraryVersionException, NoSuchFileException, FileNotFoundException, SQLException {
    load(basePath, voidPartialPath, null, null);
    String htmlPartialPathName = voidPartialPath + ".html";
    Path htmlPartialPath = Paths.get(htmlPartialPathName);
    this.css.setPaths(basePath, htmlPartialPath);
    this.js.setPaths(basePath, htmlPartialPath);
    this.html.setPaths(basePath, htmlPartialPath);
  }

  void load(Path basePath, Path voidPartialPath, String id, Caller caller) throws IOException, SiteCreationException, LibraryVersionException, NoSuchFileException, FileNotFoundException, SQLException {
    String jsonPartialPathName = voidPartialPath + ".json";
    String htmlPartialPathName = voidPartialPath + ".html";
    Path htmlPartialPath = Paths.get(htmlPartialPathName);
    Path htmlSourceFilePath = getSourceFilePath(basePath, htmlPartialPathName);
    Logger.debug("Loadding %s file into HTML container.", htmlSourceFilePath);

    this.actual = html;
    Path jsonPartialPath = Paths.get(jsonPartialPathName);
    Path jsonSourceFilePath = basePath.resolve(jsonPartialPath);
    if (Files.isRegularFile(jsonSourceFilePath)) {
      Logger.debug("FOUND configuration file %s for %s.", jsonSourceFilePath, htmlPartialPathName);
      List<String> lines = Files.readAllLines(jsonSourceFilePath);
      StringBuilder sb = new StringBuilder();
      int lineNumber = 1;
      for (String line : lines) {
        try {
          sb.append(templateVariables.replace(line, lineNumber, jsonSourceFilePath));
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
      if (templateName == null) {
        loadCSS(basePath, htmlPartialPath, id, caller);
        loadHTML(basePath, htmlPartialPath, id, caller);
      } else {
        // Read template from template files
        Path commonsComponentsTemplatePath = Configuration.getInstance().getCommonsComponentsTemplatesPath();
        Path htmlSourceTemplateFilePath = Paths.get(templateName + ".html");
        Path cssSourceTemplateFilePath = Paths.get(templateName + ".css");
        Path htmlSourceTemplateFullFilePath = commonsComponentsTemplatePath.resolve(htmlSourceTemplateFilePath);

        Logger.debug("Load template %s from file %s.", htmlSourceTemplateFullFilePath, jsonPartialPath);
        jsonObject.remove("template");
        loadCSS(commonsComponentsTemplatePath, cssSourceTemplateFilePath, id, caller);
        loadHTML(commonsComponentsTemplatePath, htmlSourceTemplateFilePath, id, caller);
        // TODO read images from template
      }
      if (id != null) {
        JSONObject idJSONObject = new JSONObject();
        idJSONObject.add(new JSONPair(id, jsonObject));
        templateVariables.add(idJSONObject);
      } else {
        templateVariables.add(jsonObject);
      }
    } else {
      loadHTML(basePath, htmlPartialPath, id, caller);
    }
  }

  private void loadCSS(Path basePath, Path cssPartialPath, String id, Caller caller) throws IOException {
    Path cssSourceFilePath = basePath.resolve(cssPartialPath);
    if (!Files.exists(cssSourceFilePath)) {
      return;
    }
    Logger.debug("Load the CSS file source %s.", cssSourceFilePath);

    int lineNumber = 0;
    css.add(new CodeLine("/* created by system using " + cssPartialPath + " called from " + caller + " */", lineNumber));
    try (Stream<String> lines = Files.lines(cssSourceFilePath, Configuration.getInstance().getEncoding())) {
      for (String line : (Iterable<String>) lines::iterator) {
        lineNumber++;
        line = addIdToTemplateVariables(line, id);
        css.add(new CodeLine(line, lineNumber));
      }
    }
  }

  private void loadHTML(Path basePath, Path partialPath, String id, Caller caller) throws IOException, NoSuchFileException, FileNotFoundException, SiteCreationException, LibraryVersionException, SQLException {
    if (partialPath.isAbsolute()) {
      throw new RuntimeException("The path " + partialPath + " is absolute.");
    }
    Path sourceFilePath = basePath.resolve(partialPath);
    Logger.debug("Load the HTML file source %s.", sourceFilePath);

    int lineNumber = 0;
    try (Stream<String> lines = Files.lines(sourceFilePath, Configuration.getInstance().getEncoding())) {
      for (String line : (Iterable<String>) lines::iterator) {
        lineNumber++;
        if (lineNumber == 1) {
          if (!"<!DOCTYPE html>".equals(line.trim()) && caller == null) {
            throw new SiteCreationException("The page " + sourceFilePath + " is not a main page.");
          }
        }
        line = addIdToTemplateVariables(line, id);
        processHTML(sourceFilePath.getParent(), partialPath, line, id, lineNumber, caller);
      }
    } catch (InvalidFragmentTag e) {
      Position position = new Position(lineNumber, e.getRow());
      throw new LocatedSiteCreationException(e.getMessage(), sourceFilePath, position);
    }
  }

  private String addIdToTemplateVariables(String line, String id) {
    StringBuilder sb = new StringBuilder();
    int i;
    int last = 0;

    while ((i = line.indexOf("#{", last)) != -1) {
      sb.append(line.substring(last, i));
      last = line.indexOf("}", i);
      String name = line.substring(i + 2, last);
      last++;
      sb.append("#{").append(id).append(".").append(name).append("}");
    }
    sb.append(line.substring(last));
    return sb.toString();
  }

  private void processHTML(Path basePath, Path partialPath, String fullLine, String id, int lineNumber, Caller caller) throws IOException, NoSuchFileException, FileNotFoundException, InvalidFragmentTag, SiteCreationException, LibraryVersionException, SQLException {

    String changedLine;
    if (id != null) {
      changedLine = addTemplateLiteralVariableToId(fullLine, id);
    } else {
      changedLine = fullLine;
    }

    String line = changedLine.trim();

    // TODO validar que la inclusión del estilo se encuentre dentro de head
    if (line.startsWith("<style file=") && line.endsWith("</style>")) {
      String fileName = line.substring(13, line.length() - 10);
      Path fullPath = basePath.resolve(fileName);
      Logger.debug("Load %s from %s.", fileName, fullPath);
      if (!Files.exists(fullPath)) {
        throw new FileNotFoundException("File not found: " + fileName);
      }
      SofiaCascadingStyleSheetSource cssFile = new SofiaCascadingStyleSheetSource(caller);
      cssFile.setPaths(basePath, partialPath);
      cssContainer.load(cssFile); // Explicit css file on tag
      return;
    }
    do {
      if (line.startsWith("<html")) {
        if (caller != null) {
          throw new InvalidFragmentTag("A HTML fragment can't have the <html> tag", 0);
        }
        int i = line.indexOf("profiles");
        if (i > 0) {
          String profileString = line.substring(i + 10, line.length() - 2);
          Logger.debug("Profiles: " + profileString);
          String[] ps = profileString.split(",");
          profiles = ProfileManager.getInstance().createFromNames(ps, site);
        }
        actual = html;
        if (caller != null) {
          actual.add(new CodeLine("<html>\n", lineNumber));
        }
        break;
      }
      if (line.startsWith("<script lib=\"") && line.endsWith("\"></script>")) {
        String libraryReference = line.substring(13, line.length() - 11);
        Logger.debug("Library reference name found: %s.", libraryReference);

        Caller newCaller = new Caller(Configuration.getInstance().getCommonsLibsPath(), partialPath, lineNumber);
        Library library = new Library(libraryReference, newCaller);
        libraries.add(library);
        break;
      }
      switch (line) {
        case "<style>":
          actual = css;
          actual.add(new CodeLine("/* created by system using " + partialPath + " called from " + caller + " */", lineNumber));
          break;
        case "<script>":
          actual = js;
          actual.add(new CodeLine("//( created by system using " + partialPath + " called from " + caller, lineNumber));
          break;
        case "</html>":
          if (caller != null) {
            actual.add(new CodeLine("</html>\n", lineNumber));
          }
          actual = html;
          break;
        case "</style>":
        case "</script>":
          actual = html;
          break;
        default:
          if (actual.getType() == SofiaSource.Type.HTML) {
            String l = getProcessedLine(basePath, partialPath, changedLine, line, lineNumber, caller);
            actual.add(new CodeLine(l, lineNumber));
            break;
          }
          actual.add(new CodeLine(changedLine, lineNumber));
          break;
      }
    } while (false);
  }

  private String getProcessedLine(Path basePath, Path partialPath, String fullLine, String line, int lineNumber, Caller caller) throws NoSuchFileException, IOException, FileNotFoundException, InvalidFragmentTag, SiteCreationException, LibraryVersionException, SQLException {
    StringBuilder sb = new StringBuilder();

    Tag tag = HTMLTagFactory.get(line);
    if (tag != null && tag.isSection()) {
      if (tag.getValue("file") != null) {
        tag.rename("div");
        actual.add(new CodeLine(tag.getStartTag(), lineNumber));
        String fileName = tag.getValue("file");
        Path newFileBasePath;
        if (fileName.startsWith("/")) {
          newFileBasePath = site.getSourcesPath();
          fileName = fileName.substring(1);
        } else {
          newFileBasePath = basePath;
        }
        Path filePath = Paths.get(fileName);
        try {
          Caller newCaller = new Caller(basePath, partialPath, lineNumber);
          load(newFileBasePath, filePath, tag.getId(), newCaller);
        } catch (NoSuchFileException e) {
          throw new NoSuchFileException("Can't find the file " + fileName + " called from " + partialPath + ":" + lineNumber);
        }
        actual.add(new CodeLine(tag.getEndTag(), lineNumber));
        return sb.toString();
      }
      if (tag.getValue("template") != null) {
        tag.rename("div");
        actual.add(new CodeLine(tag.getStartTag(), lineNumber));
        String templateName = tag.getValue("template");
        Path templatePath = Paths.get(templateName);
        if (tag.getId() == null) {
          throw new InvalidFragmentTag("The fragment tag must have an id.", tag.getColumn());
        }
        try {
          Caller newCaller = new Caller(basePath, partialPath, lineNumber);
          load(Configuration.getInstance().getCommonsComponentsTemplatesPath(), templatePath, tag.getId(), newCaller);
          String configurationFile = tag.getValue("configurationFile");

          Path newPartialPath;
          if (configurationFile == null) {
            newPartialPath = Paths.get(tag.getId() + ".json");
          } else {
            newPartialPath = Paths.get(configurationFile);
          }
          readJSONFileForId(basePath, newPartialPath, tag.getId(), caller);

          addImagesResources(Configuration.getInstance().getCommonsComponentsTemplatesPath(), templateName);
        } catch (NoSuchFileException e) {
          throw new NoSuchFileException("No such template file: " + templateName);
        }
        actual.add(new CodeLine(tag.getEndTag(), lineNumber));
        return sb.toString();
      }
    }

    sb.append(fullLine);
    return sb.toString();
  }

  private void readJSONFileForId(Path basePath, Path partialPath, String id, Caller caller) throws IOException, SiteCreationException {
    Path jsonSourceFilePath = basePath.resolve(partialPath);
    if (Files.isRegularFile(jsonSourceFilePath)) {
      Logger.debug("FOUND configuration file %s for %s.", jsonSourceFilePath, id);
      SofiaJSONSource jsonCode = new SofiaJSONSource(caller);
      jsonCode.setPaths(basePath, partialPath);
      jsonCode.load();
      try {
        String code = jsonCode.getCode();
        JSONObject jsonObject = JSON.parse(code).toJSONObject();
        JSONPair jsonPair = new JSONPair(id, jsonObject);
        JSONObject jsonTemplateObject = new JSONObject();
        jsonTemplateObject.add(jsonPair);
        templateVariables.add(jsonTemplateObject);
      } catch (JSONParseException e) {
        throw new SiteCreationException("Cant parse " + jsonSourceFilePath + ". " + e.getMessage());
      }
    }
  }

  private void addImagesResources(Path commonsComponentsTemplatesPath, String templateName) {
    Path origin = commonsComponentsTemplatesPath.resolve(templateName).getParent().resolve("images");
    Path target = site.getImagesPath().resolve(templateName).getParent();
    Resource resource = new Resource(origin, target);
//    resources.add(resource);
  }

  private Path getSourceFilePath(Path basePath, String fileName) {
    if (fileName.startsWith("/")) {
      return Configuration.getInstance().getCommonSourcesPath().resolve(fileName.substring(1));
    } else {
      return basePath.resolve(fileName);
    }
  }

  private String addTemplateLiteralVariableToId(String line, String id) {
    int i;
    StringBuilder sb = new StringBuilder();
    while ((i = line.indexOf("#{")) != -1) {
      sb.append(line.substring(0, i + 2));
      sb.append(id).append('.');
      line = line.substring(i + 2);
    }
    sb.append(line);
    return sb.toString();
  }

  Profiles getProfiles() {
    return profiles;
  }

  @Override
  void save() throws SiteCreationException, IOException {
    Logger.debug("Creating the file %s.", getTargetFilePath());
    Files.write(getTargetFilePath(), getCode());
  }

  private byte[] getCode() {
    String code = html.getCode();
    return code.getBytes(Configuration.getInstance().getEncoding());
  }

  Libraries getLibraries() {
    return libraries;
  }

  @Override
  protected void apply(TemplateVariables templateVariables) throws UndefinedLiteralException {
    super.apply(html, templateVariables);
  }
}
