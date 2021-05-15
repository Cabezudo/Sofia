package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.files.FileHelper;
import net.cabezudo.sofia.core.html.Tag;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.08
 */
class HTMLTemplateLine extends HTMLFileLine {

  public HTMLTemplateLine(Site site, Path basePath, Path parentPath, TemplateVariables templateVariables, TextsFile textsFile, Tag tag, int lineNumber, Caller caller)
          throws IOException, SiteCreationException, LocatedSiteCreationException, InvalidFragmentTag, LibraryVersionConflictException, JSONParseException {
    super(site, basePath, parentPath, templateVariables, textsFile, tag, lineNumber, caller);
  }

  @Override
  void load() throws InvalidFragmentTag, IOException, SiteCreationException, LocatedSiteCreationException, LibraryVersionConflictException, JSONParseException, ClusterException {
    if (getTag().getId() == null) {
      throw new InvalidFragmentTag("A template tag must have an id.", getFilePath(), new Position(getLineNumber(), 0));
    }
    super.load();
  }

  @Override
  Path getConfigurationFilePath(Caller caller) {
    Path commonsHTMLTemplatesPath = Configuration.getInstance().getCommonsHTMLTemplatesPath();
    String templateName = getTag().getValue("template");
    Logger.debug("Get configuration file path from template tag attribute: %s", templateName);
    return commonsHTMLTemplatesPath.resolve(FileHelper.removeExtension(templateName) + ".json");
  }

  @Override
  Path getTextsFilePath(Caller caller) {
    String fileName = getFilePath().toString();
    String textsFileName = FileHelper.removeExtension(fileName) + ".texts.json";

    Path textsFilePath;
    if (caller == null) {
      textsFilePath = getSite().getBasePath().resolve(textsFileName);
    } else {
      textsFilePath = caller.getBasePath().resolve(caller.getRelativePath()).getParent().resolve(textsFileName);
    }
    Logger.debug("Text file path: %s", textsFilePath);
    return textsFilePath;
  }

  @Override
  Path getFilePath() {
    String templateName = getTag().getValue("template");
    return Paths.get(templateName);

  }

  @Override
  HTMLSourceFile getHTMLSourceFile(Caller caller) throws IOException, SiteCreationException, LocatedSiteCreationException {
    Path templatesBasePath = Configuration.getInstance().getCommonsHTMLTemplatesPath();
    Logger.debug("Create new HTMLTemplateSectionSourceFile using %s.", templatesBasePath);
    return new HTMLTemplateSectionSourceFile(getSite(), templatesBasePath, getFilePath(), getTag().getId(), getTemplateVariables(), getTextsFile(), caller);
  }
}
