package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.cabezudo.json.JSON;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.files.FileHelper;
import net.cabezudo.sofia.core.html.Tag;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.12.04
 */
public abstract class HTMLFileLine extends Line {

  private final Site site;
  private final Path basePath;
  private final TemplateVariables templateVariables;
  private final TextsFile textsFile;
  private final Tag tag;
  private final Caller caller;
  private final Line startLine;
  private final Line endLine;
  private HTMLSourceFile htmlSourceFile;

  HTMLFileLine(Site site, Path basePath, Path parentPath, TemplateVariables templateVariables, TextsFile textsFile, Tag tag, int lineNumber, Caller caller)
          throws IOException, SiteCreationException, LocatedSiteCreationException, InvalidFragmentTag, LibraryVersionConflictException, JSONParseException {
    super(lineNumber);

    this.site = site;
    this.basePath = basePath;
    this.templateVariables = templateVariables;
    this.textsFile = textsFile;
    this.tag = tag;
    this.caller = caller;
    Logger.debug("basePath: %s.", this.basePath);

    tag.rename("div");
    startLine = new CodeLine(tag.getStartTag(), lineNumber);
    endLine = new CodeLine(tag.getEndTag(), lineNumber);
  }

  void load() throws InvalidFragmentTag, IOException, SiteCreationException, LocatedSiteCreationException, LibraryVersionConflictException, JSONParseException, ClusterException {
    Logger.debug("Load file line %s.", getFilePath());

    Path configurationFilePath;
    String configurationFileTagValue = tag.getValue("configurationFile");
    if (configurationFileTagValue == null) {
      Logger.debug("configurationFile tag attribute NOT FOUND.");
      configurationFilePath = getConfigurationFilePath(caller);
    } else {
      Logger.debug("configurationFile tag attribute FOUND: %s", configurationFileTagValue);
      if (configurationFileTagValue.startsWith("/")) {
        configurationFilePath = site.getBasePath().resolve(configurationFileTagValue);
      } else {
        Path fullPath = caller.getFullPath();
        Path parent = fullPath.getParent();
        configurationFilePath = parent.resolve(configurationFileTagValue);
      }
    }

    Logger.debug("Search configuration file %s.", configurationFilePath);
    JSONObject jsonConfiguration = null;
    if (Files.exists(configurationFilePath)) {
      Logger.debug("Load configuration file %s.", configurationFilePath);
      jsonConfiguration = JSON.parse(configurationFilePath, Configuration.getInstance().getEncoding().toString()).toJSONObject();
    } else {
      Logger.debug("Configuration file %s NOT FOUND.", configurationFilePath);
      jsonConfiguration = new JSONObject();
    }
    readTextsFile();

    String tagId = tag.getId();
    String pageReference = jsonConfiguration.getNullString("page");

    if (pageReference == null) {
      templateVariables.merge(jsonConfiguration);
      htmlSourceFile = getHTMLSourceFile(caller);
      htmlSourceFile.loadHTMLFile();

      Path cssBasePath = htmlSourceFile.getBasePath();
      Path htmlPartialPath = htmlSourceFile.getPartialFilePath();
      String cssPartialName = FileHelper.removeExtension(htmlPartialPath) + ".css";
      htmlSourceFile.loadCSSFile(cssBasePath, cssPartialName, caller);
      JSONObject jsonObject;
      System.out.println("configurationFilePath: " + configurationFilePath);
      System.out.println("configurationFilePath.getParent(): " + configurationFilePath.getParent());
      System.out.println("tagId: " + tagId);

      Path jsonIdConfigurationFilePath = configurationFilePath.getParent().resolve(tagId + ".json");
      Logger.debug("Search configuration file %s for id %s.", jsonIdConfigurationFilePath, tagId);
      if (Files.isRegularFile(jsonIdConfigurationFilePath)) {
        Logger.debug("FOUND configuration file %s for id %s.", jsonIdConfigurationFilePath, tagId);
        try {
          jsonObject = JSON.parse(jsonIdConfigurationFilePath, Configuration.getInstance().getEncoding().toString()).toJSONObject();
          setTemplateVariablesForId(jsonObject, tagId, templateVariables);
        } catch (JSONParseException e) {
          throw new SiteCreationException("Can't parse " + jsonIdConfigurationFilePath + ". " + e.getMessage());
        }
      }
    } else {
      Logger.debug("The configuration file has a page property. Load page %s.", pageReference);
      jsonConfiguration.remove("page");
      Path commonsHTMLTemplatesPath = Configuration.getInstance().getCommonsHTMLTemplatesPath();
      Path voidPagePath = Paths.get(pageReference);

      Logger.debug("Load page %s.", voidPagePath);

      Caller pageCaller = new Caller(getBasePath(), configurationFilePath, 0, caller);
      htmlSourceFile = new HTMLPageSourceFile(getSite(), commonsHTMLTemplatesPath, voidPagePath, getTemplateVariables(), textsFile, pageCaller);
      htmlSourceFile.loadHTMLFile();
    }
  }

  abstract Path getConfigurationFilePath(Caller caller);

  abstract Path getTextsFilePath(Caller caller);

  abstract Path getFilePath();

  abstract HTMLSourceFile getHTMLSourceFile(Caller caller)
          throws IOException, SiteCreationException, LocatedSiteCreationException, LibraryVersionConflictException, JSONParseException;

  private void readTextsFile() throws JSONParseException, IOException {
    Path textsFilePath = getTextsFilePath(caller);
    Logger.debug("Search texts file %s.", textsFilePath);
    if (Files.exists(textsFilePath)) {
      Logger.debug("Load texts file %s.", textsFilePath);
      JSONObject jsonObject = JSON.parse(textsFilePath, Configuration.getInstance().getEncoding().toString()).toJSONObject();
      textsFile.add(jsonObject);
    } else {
      Logger.debug("Texts file %s NOT FOUND.", textsFilePath);
    }
  }

  private void setTemplateVariablesForId(JSONObject jsonObject, String tagId, TemplateVariables templateVariables) throws IOException, SiteCreationException {
    JSONPair jsonPair = new JSONPair(tagId, jsonObject);
    JSONObject jsonTemplateObject = new JSONObject();
    jsonTemplateObject.add(jsonPair);
    templateVariables.merge(jsonTemplateObject);
  }

  Tag getTag() {
    return tag;
  }

  Site getSite() {
    return site;
  }

  Path getBasePath() {
    return basePath;
  }

  TemplateVariables getTemplateVariables() {
    return templateVariables;
  }

  TextsFile getTextsFile() {
    return textsFile;
  }

  @Override
  Libraries getLibraries() {
    return htmlSourceFile.getLibraries();
  }

  @Override
  String getCode() {
    StringBuilder sb = new StringBuilder();
    sb.append(startLine.getCode()).append('\n');
    sb.append(htmlSourceFile.getCode());
    sb.append(endLine.getCode()).append('\n');
    return sb.toString();
  }

  @Override
  Line replace(TemplateVariables templateVariables) throws UndefinedLiteralException {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  boolean isEmpty() {
    return false;
  }

  @Override
  boolean isNotEmpty() {
    return true;
  }

  @Override
  boolean startWith(String start) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  boolean endWith(String end) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int compareTo(Line o) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  Lines getJavaScriptLines() {
    return htmlSourceFile.getJavaScriptLines();
  }

  @Override
  CSSImports getCascadingStyleSheetImports() {
    return htmlSourceFile.getCascadingStyleSheetImports();
  }

  @Override
  Lines getCascadingStyleSheetLines() {
    return htmlSourceFile.getCascadingStyleSheetLines();
  }
}
