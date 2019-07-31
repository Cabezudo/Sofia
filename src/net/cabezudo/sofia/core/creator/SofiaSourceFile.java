package net.cabezudo.sofia.core.creator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;
import net.cabezudo.json.JSON;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.configuration.Configuration;
import static net.cabezudo.sofia.core.creator.SofiaSourceContainer.Type.HTML;
import net.cabezudo.sofia.core.html.HTMLTagFactory;
import net.cabezudo.sofia.core.html.Tag;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.23
 */
public class SofiaSourceFile {

  private final Set<Path> filePaths = new TreeSet<>();
  private final Set<Resource> resources = new TreeSet<>();
  private String profiles;
  private final SofiaSourceContainer html;
  private final SofiaSourceContainer css;
  private final SofiaSourceContainer js;
  private SofiaSourceContainer actual;
  private final Site site;
  private final TemplateLiterals templateLiterals;
  private int deep = 0;

  SofiaSourceFile(Site site, TemplateLiterals templateLiterals, String voidPartialPath) throws IOException, SQLException, FileNotFoundException, JSONParseException, NoSuchFileException, SiteCreationException {
    this.site = site;
    this.templateLiterals = templateLiterals;
    this.html = new HTMLSource(templateLiterals);
    this.css = new CascadingStyleSheetsCode(templateLiterals);
    this.js = new JavaScriptCode(templateLiterals);
    this.actual = html;
    load(null, site.getSourcesPath(), voidPartialPath);
  }

  // Use the base path and file name to load a file into html, css and js. Create a list of read files in filePaths
  public final void load(String id, Path basePath, String voidPartialPath) throws IOException, FileNotFoundException, NoSuchFileException, SiteCreationException {
    deep++;
    String jsonPartialPath = voidPartialPath + ".json";
    String htmlPartialPath = voidPartialPath + ".html";

    Path jsonSourceFilePath = basePath.resolve(jsonPartialPath);
    if (Files.isRegularFile(jsonSourceFilePath)) {
      Logger.debug("FOUND configuration file %s for %s.", jsonSourceFilePath, htmlPartialPath);
      String jsonCode = new String(Files.readAllBytes(jsonSourceFilePath), Charset.forName("UTF-8"));
      try {
        jsonCode = templateLiterals.apply(jsonCode);
      } catch (UndefinedLiteralException e) {
        Path safePath = Configuration.getInstance().getCommonsTemplatesPath().relativize(jsonSourceFilePath);
        throw new SiteCreationException("Undefined literal " + e.getUndefinedLiteral() + " in " + safePath + ".");
      }
      JSONObject jsonObject;
      try {
        jsonObject = JSON.parse(jsonCode).toJSONObject();
      } catch (JSONParseException e) {
        throw new SiteCreationException("Cant parse " + jsonSourceFilePath + ". " + e.getMessage());
      }
      String templateName = jsonObject.getNullString("template");
      if (templateName == null) {
        loadHTML(basePath, htmlPartialPath, id);
      } else {
        // Read template from template files
        Path commonsComponentsTemplatePath = Configuration.getInstance().getCommonsComponentsTemplatesPath();
        System.out.println(commonsComponentsTemplatePath);
        System.out.println(basePath);
        System.out.println(templateName);
//        System.out.println(voidPartialPath);

        String htmlTemplateName = templateName + ".html";
        Path htmlSourceFilePath = commonsComponentsTemplatePath.resolve(htmlTemplateName);
        System.out.println("htmlSourceFilePath: " + htmlSourceFilePath);

        Logger.debug("Load template %s from file %s.", htmlSourceFilePath.relativize(commonsComponentsTemplatePath), jsonPartialPath);
        jsonObject.remove("template");
        loadHTML(Configuration.getInstance().getCommonsComponentsTemplatesPath(), htmlTemplateName, id);
        // TODO read images from template
      }
      if (id != null) {
        JSONObject idJSONObject = new JSONObject();
        idJSONObject.add(new JSONPair(id, jsonObject));
        templateLiterals.add(idJSONObject);
      } else {
        templateLiterals.add(jsonObject);
      }
    } else {
      loadHTML(basePath, htmlPartialPath, id);
    }
    deep--;
  }

  private void loadHTML(Path basePath, String htmlFilename, String id) throws IOException, NoSuchFileException, FileNotFoundException, SiteCreationException {
    Path sourceFilePath = getSourceFilePath(basePath, htmlFilename);
    Logger.debug("Load the HTML file source %s.", sourceFilePath);
    filePaths.add(sourceFilePath);

    int lineNumber = 0;
    try (Stream<String> lines = Files.lines(sourceFilePath, StandardCharsets.UTF_8)) {
      for (String line : (Iterable<String>) lines::iterator) {
        lineNumber++;
        if (lineNumber == 1) {
          if (!"<!DOCTYPE html>".equals(line.trim()) && deep == 1) {
            throw new SiteCreationException("The page " + htmlFilename + " is not a main page.");
          }
        }
        process(sourceFilePath.getParent(), line, id);
      }
    } catch (InvalidFragmentTag e) {
      throw new SiteCreationException(e.getMessage(), e, htmlFilename, lineNumber, e.getCol());
    }
  }

  private Path getSourceFilePath(Path basePath, String fileName) {
    if (fileName.startsWith("/")) {
      return Configuration.getInstance().getSourcesPath().resolve(fileName.substring(1));
    } else {
      return basePath.resolve(fileName);
    }
  }

  private String addIdInTemplateLiteralVariable(String line, String id) {
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

  private void process(Path basePath, String fullLine, String id) throws IOException, NoSuchFileException, FileNotFoundException, InvalidFragmentTag, SiteCreationException {

    String changedLine;
    if (id != null) {
      changedLine = addIdInTemplateLiteralVariable(fullLine, id);
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
      css.append(fullPath);
      return;
    }
    if (line.startsWith("<html")) {
      if (deep != 1) {
        throw new InvalidFragmentTag("A HTML fragment can't have the <html> tag", 0);
      }
      int i = line.indexOf("profiles");
      if (i > 0) {
        this.profiles = line.substring(i + 10, line.length() - 2);
      }
      actual = html;
      if (deep == 1) {
        actual.append("<html>").append('\n');
      }
    } else {
      switch (line) {
        case "<style>":
          actual = css;
          break;
        case "<script>":
          actual = js;
          break;
        case "</html>":
          if (deep == 1) {
            actual.append("</html>").append('\n');
          }
          actual = html;
          break;
        case "</style>":
        case "</script>":
          actual = html;
          break;
        default:
          if (actual.getType() == HTML) {
            actual.append(getProcessedLine(basePath, changedLine, line));
            break;
          }
          actual.append(changedLine);
          actual.append('\n');
          break;
      }
    }
  }

  private String getProcessedLine(Path basePath, String fullLine, String line) throws NoSuchFileException, IOException, FileNotFoundException, InvalidFragmentTag, SiteCreationException {
    StringBuilder sb = new StringBuilder();

    Tag tag = HTMLTagFactory.get(line);
    if (tag != null && tag.isSection()) {
      if (tag.getValue("file") != null) {
        String fileName = tag.getValue("file");
        try {
          load(tag.getId(), basePath, fileName);
        } catch (NoSuchFileException e) {
          throw new NoSuchFileException("No such file: " + fileName);
        }
        return sb.toString();
      }
      if (tag.getValue("template") != null) {
        String templateName = tag.getValue("template");
        if (tag.getId() == null) {
          throw new InvalidFragmentTag("The fragment tag must have an id.", tag.getColumn());
        }
        try {
          load(tag.getId(), Configuration.getInstance().getCommonsComponentsTemplatesPath(), templateName);
          readJSONFileForId(basePath, tag.getId());
          addImagesResources(Configuration.getInstance().getCommonsComponentsTemplatesPath(), templateName);
        } catch (NoSuchFileException e) {
          throw new NoSuchFileException("No such template file: " + templateName);
        }
        return sb.toString();
      }
    }

    sb.append(fullLine);
    sb.append('\n');
    return sb.toString();
  }

  private void readJSONFileForId(Path basePath, String jsonFileName) throws IOException, SiteCreationException {
    Path jsonSourceFilePath = basePath.resolve(jsonFileName + ".json");
    if (Files.isRegularFile(jsonSourceFilePath)) {
      Logger.debug("FOUND configuration file %s for %s.", jsonSourceFilePath, jsonFileName);
      String jsonCode = new String(Files.readAllBytes(jsonSourceFilePath), Charset.forName("UTF-8"));
      try {
        jsonCode = templateLiterals.apply(jsonCode);
        try {
          JSONObject jsonObject = JSON.parse(jsonCode).toJSONObject();
          JSONPair jsonPair = new JSONPair(jsonFileName, jsonObject);
          JSONObject jsonTemplateObject = new JSONObject();
          jsonTemplateObject.add(jsonPair);
          templateLiterals.add(jsonTemplateObject);
        } catch (JSONParseException e) {
          throw new SiteCreationException("Cant parse " + jsonSourceFilePath + ". " + e.getMessage());
        }
      } catch (UndefinedLiteralException e) {
        Path safePath = Configuration.getInstance().getCommonsTemplatesPath().relativize(jsonSourceFilePath);
        throw new SiteCreationException("Undefined literal " + e.getUndefinedLiteral() + " in " + safePath + ".");
      }
    }
  }

  private void addImagesResources(Path commonsComponentsTemplatesPath, String templateName) {
    Path origin = commonsComponentsTemplatesPath.resolve(templateName).getParent().resolve("images");
    Path target = site.getImagesPath().resolve(templateName).getParent();
    Resource resource = new Resource(origin, target);
    resources.add(resource);
  }

  Set<Resource> getResources() {
    return resources;
  }

  String getProfiles() {
    return profiles;
  }

  String getContent() {
    return actual.getCode();
  }

  Set<Path> getFiles() {
    return filePaths;
  }

  String getHTMLCode() {
    return html.getCode();
  }

  String getCascadingStyleSheetsCode() {
    return css.getCode();
  }

  String getJavaScriptCode() {
    return js.getCode();
  }
}
