package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import net.cabezudo.json.JSON;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.25
 */
class BaseHTMLSourceFile {

  private final TemplateVariables templateVariables;
  private final CascadingStyleSheetSourceFile css;
  private final JavaScriptSourceFile js;
  private final HTMLSourceFile html;
  private Site site;
  private final Path cssPartialPath;
  private final Path jsPartialPath;
  private final Path htmlPartialPath;

  BaseHTMLSourceFile(Site site, TemplateVariables templateVariables, Path voidPartialPath) throws IOException, SiteCreationException {
    this.site = site;
    this.templateVariables = templateVariables;

    Path basePath = site.getSourcesPath();
    String jsonPartialPathName = voidPartialPath + ".json";
    String cssPartialPathName = voidPartialPath + ".css";
    String jsPartialPathName = voidPartialPath + ".js";
    String htmlPartialPathName = voidPartialPath + ".html";
    cssPartialPath = Paths.get(cssPartialPathName);
    jsPartialPath = Paths.get(jsPartialPathName);
    htmlPartialPath = Paths.get(htmlPartialPathName);
    Path htmlSourceFilePath = basePath.resolve(htmlPartialPathName);
    Logger.debug("Loadding %s HTML base file.", htmlSourceFilePath);

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
        css = new CascadingStyleSheetSourceFile(basePath, cssPartialPath, templateVariables, null);
        js = new JavaScriptSourceFile(basePath, cssPartialPath, templateVariables, null);
        html = new HTMLSourceFile(basePath, htmlPartialPath, templateVariables, null);
      } else {
        // Read template from template files
        Path commonsComponentsTemplatePath = Configuration.getInstance().getCommonsComponentsTemplatesPath();
        Path cssSourceTemplateFilePath = Paths.get(templateName + ".css");
        Path jsSourceTemplateFilePath = Paths.get(templateName + ".js");
        Path htmlSourceTemplateFilePath = Paths.get(templateName + ".html");
        Path htmlSourceTemplateFullFilePath = commonsComponentsTemplatePath.resolve(htmlSourceTemplateFilePath);

        Logger.debug("Load template %s from file %s.", htmlSourceTemplateFullFilePath, jsonPartialPath);
        jsonObject.remove("template");
        css = new CascadingStyleSheetSourceFile(commonsComponentsTemplatePath, cssSourceTemplateFilePath, templateVariables, null);
        js = new JavaScriptSourceFile(commonsComponentsTemplatePath, jsSourceTemplateFilePath, templateVariables, null);
        html = new HTMLSourceFile(commonsComponentsTemplatePath, htmlSourceTemplateFilePath, templateVariables, null);
        // TODO read images from template
      }
    } else {
      css = new CascadingStyleSheetSourceFile(basePath, cssPartialPath, templateVariables, null);
      js = new JavaScriptSourceFile(basePath, jsPartialPath, templateVariables, null);
      html = new HTMLSourceFile(basePath, htmlPartialPath, templateVariables, null);
    }
  }

  void save() throws IOException {
//    Path cssFilePath = site.getCSSPath().resolve(cssPartialPath);
//    Logger.debug("Creating the CSS file %s.", cssFilePath);
//    StringBuilder cssCode = new StringBuilder();
//    for (Line line : css.getImports()) {
//      cssCode.append(line).append('\n');
//    }
//    for (Line line : css.getLines()) {
//      cssCode.append(line).append('\n');
//    }
//    Files.write(cssFilePath, cssCode.toString().getBytes(Configuration.getInstance().getEncoding()));
//
//    Path jsFilePath = site.getJSPath().resolve(jsPartialPath);
//    Logger.debug("Creating the JS file %s.", cssFilePath);
//    StringBuilder jsCode = new StringBuilder();
//    for (Line line : js.getLines()) {
//      jsCode.append(line).append('\n');
//    }
//    Files.write(jsFilePath, jsCode.toString().getBytes(Configuration.getInstance().getEncoding()));

    Path htmlFilePath = site.getVersionPath().resolve(htmlPartialPath);
    Logger.debug("Creating the HTML file %s.", htmlFilePath);
    StringBuilder htmlCode = new StringBuilder();
    for (Line line : html.getHTMLLines()) {
      htmlCode.append(line).append('\n');
    }
    Files.write(htmlFilePath, htmlCode.toString().getBytes(Configuration.getInstance().getEncoding()));
  }

  Lines getCSSLines() {
    return css.getLines();
  }

  Path getPartialPath() {
    return htmlPartialPath;
  }
}
