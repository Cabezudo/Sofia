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
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.12.04
 */
public class HTMLTemplateLine extends Line {

  private final Line startLine;
  private final HTMLTemplateSourceFile file;
  private final Line endLine;

  HTMLTemplateLine(Site site, Path basePath, Path parentPath, TemplateVariables templateVariables, Libraries libraries, Tag tag, int lineNumber) throws IOException, SiteCreationException, LocatedSiteCreationException, SQLException, InvalidFragmentTag {
    super(lineNumber);

    tag.rename("div");
    startLine = new CodeLine(tag.getStartTag(), lineNumber);
    endLine = new CodeLine(tag.getEndTag(), lineNumber);
    String templateName = tag.getValue("template");
    Path templatePath = Paths.get(templateName + ".html");
    if (tag.getId() == null) {
      throw new InvalidFragmentTag("A template tag must have an id.", tag.getColumn());
    }
    try {
      Caller newCaller = new Caller(basePath, parentPath, lineNumber);
      Path templatesBasePath = Configuration.getInstance().getCommonsComponentsTemplatesPath();
      file = new HTMLTemplateSourceFile(site, templatesBasePath, templatePath, tag.getId(), templateVariables, newCaller, libraries);
      file.loadJSONConfigurationFile();
      String configurationFile = tag.getValue("configurationFile");

      Path newPartialPath;
      if (configurationFile == null) {
        newPartialPath = Paths.get(tag.getId() + ".json");
      } else {
        newPartialPath = Paths.get(configurationFile);
      }
      readJSONFileForId(basePath, newPartialPath, tag.getId(), templateVariables);
      file.loadHTMLFile();

//      addImagesResources(Configuration.getInstance().getCommonsComponentsTemplatesPath(), templateName);
    } catch (NoSuchFileException e) {
      throw new NoSuchFileException("No such template file: " + templateName);
    }
  }

  private void readJSONFileForId(Path basePath, Path partialPath, String tagId, TemplateVariables templateVariables) throws IOException, SiteCreationException {
    Path jsonSourceFilePath = basePath.resolve(partialPath);
    if (Files.isRegularFile(jsonSourceFilePath)) {
      Logger.debug("FOUND template configuration file %s for id %s.", jsonSourceFilePath, tagId);
      try {
        JSONObject jsonObject = JSON.parse(jsonSourceFilePath, Configuration.getInstance().getEncoding().toString()).toJSONObject();
        JSONPair jsonPair = new JSONPair(tagId, jsonObject);
        JSONObject jsonTemplateObject = new JSONObject();
        jsonTemplateObject.add(jsonPair);
        System.out.println(jsonTemplateObject);
        templateVariables.merge(jsonTemplateObject);
      } catch (JSONParseException e) {
        throw new SiteCreationException("Cant parse " + jsonSourceFilePath + ". " + e.getMessage());
      }
    }
  }

  @Override
  String toHTML() {
    StringBuilder sb = new StringBuilder();
    sb.append(startLine.toHTML()).append('\n');
    sb.append(file.toHTML());
    sb.append(endLine.toHTML()).append('\n');
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

}
