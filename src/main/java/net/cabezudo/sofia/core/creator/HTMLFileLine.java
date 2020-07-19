package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import net.cabezudo.json.JSON;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.html.Tag;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.12.04
 */
public abstract class HTMLFileLine extends Line {

  private final Site site;
  private final Path basePath;
  private final Path parentPath;
  private final TemplateVariables templateVariables;
  private final Tag tag;
  private final Caller caller;
  private final Line startLine;
  private final Line endLine;
  private HTMLSourceFile file;

  HTMLFileLine(Site site, Path basePath, Path parentPath, TemplateVariables templateVariables, Tag tag, int lineNumber, Caller caller)
          throws IOException, SiteCreationException, LocatedSiteCreationException, SQLException, InvalidFragmentTag, LibraryVersionConflictException, JSONParseException {
    super(lineNumber);

    this.site = site;
    this.basePath = basePath;
    this.parentPath = parentPath;
    this.templateVariables = templateVariables;
    this.tag = tag;
    this.caller = caller;

    tag.rename("div");
    startLine = new CodeLine(tag.getStartTag(), lineNumber);
    endLine = new CodeLine(tag.getEndTag(), lineNumber);
  }

  void load() throws InvalidFragmentTag, IOException, SiteCreationException, LocatedSiteCreationException, SQLException, LibraryVersionConflictException, JSONParseException {
    Logger.debug("Load file line %s.", getFilePath());
    try {
      file = getHTMLSourceFile(caller);
      String configurationFile = tag.getValue("configurationFile");

      Path newPartialPath;
      if (configurationFile == null) {
        newPartialPath = Paths.get(tag.getId() + ".json");
      } else {
        newPartialPath = Paths.get(configurationFile);
      }
      readJSONFileForId(basePath, newPartialPath, tag.getId(), templateVariables);
      file.loadHTMLFile();
    } catch (NoSuchFileException e) {
      e.printStackTrace();
      throw new NoSuchFileException("No such file: " + getFilePath());
    }
  }

  abstract Path getFilePath();

  abstract HTMLSourceFile getHTMLSourceFile(Caller caller)
          throws IOException, SiteCreationException, LocatedSiteCreationException, SQLException, LibraryVersionConflictException, JSONParseException;

  private void readJSONFileForId(Path basePath, Path partialPath, String tagId, TemplateVariables templateVariables) throws IOException, SiteCreationException {
    Path jsonSourceFilePath = basePath.resolve(partialPath);
    if (Files.isRegularFile(jsonSourceFilePath)) {
      Logger.debug("FOUND configuration file %s for id %s.", jsonSourceFilePath, tagId);
      try {
        JSONObject jsonObject = JSON.parse(jsonSourceFilePath, Configuration.getInstance().getEncoding().toString()).toJSONObject();
        JSONPair jsonPair = new JSONPair(tagId, jsonObject);
        JSONObject jsonTemplateObject = new JSONObject();
        jsonTemplateObject.add(jsonPair);
        templateVariables.merge(jsonTemplateObject);
      } catch (JSONParseException e) {
        throw new SiteCreationException("Can't parse " + jsonSourceFilePath + ". " + e.getMessage());
      }
    }
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

  @Override
  Libraries getLibraries() {
    return file.getLibraries();
  }

  @Override
  String getCode() {
    StringBuilder sb = new StringBuilder();
    sb.append(startLine.getCode()).append('\n');
    sb.append(file.getCode());
    sb.append(endLine.getCode()).append('\n');
    return sb.toString();
  }

  @Override
  Line replace(TemplateVariables templateVariables) throws UndefinedLiteralException {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  boolean isNotEmpty() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  boolean startWith(String start) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int compareTo(Line o) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  Lines getJavaScriptLines() {
    return file.getJavaScriptLines();
  }

  @Override
  CSSImports getCascadingStyleSheetImports() {
    return file.getCascadingStyleSheetImports();
  }

  @Override
  Lines getCascadingStyleSheetLines() {
    return file.getCascadingStyleSheetLines();
  }
}
