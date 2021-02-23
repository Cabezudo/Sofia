package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Path;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.12.04
 */
class HTMLFragmentSourceFile extends HTMLSourceFile {

  HTMLFragmentSourceFile(Site site, Path basePath, Path partialPath, String id, TemplateVariables templateVariables, Caller caller)
          throws IOException, LocatedSiteCreationException, SiteCreationException, InvalidFragmentTag {
    super(site, basePath, partialPath, id, templateVariables, caller);
  }

  @Override
  public boolean searchHTMLTag(SofiaSource actual, String line, Path filePath, int lineNumber) throws InvalidFragmentTag {
    if (line.startsWith("<html")) {
      throw new InvalidFragmentTag("A HTML fragment can't have the <html> tag", filePath, new Position(lineNumber, 0));
    }
    return false;
  }

  @Override
  Path getSourceFilePath(Caller caller) throws SiteCreationException {
    Path htmlSourceFilePath;
    if (getPartialFilePath().startsWith("/")) {
      htmlSourceFilePath = getSite().getVersionedSourcesPath().resolve(getPartialFilePath().toString().substring(1));
    } else {
      htmlSourceFilePath = getSite().getVersionedSourcesPath().resolve(getPartialFilePath());
    }
    Logger.debug("[HTMLFragmentSourceFile:getSourceFilePath] Full HTML fragment source file path %s.", htmlSourceFilePath);
    return htmlSourceFilePath;
  }

  @Override
  String replaceTemplateVariables(String line, int lineNumber, Path htmlSourceFilePath) throws LocatedSiteCreationException {
    try {
      return getTemplateVariables().replace(line);
    } catch (UndefinedLiteralException e) {
      Position position = new Position(lineNumber, e.getRow());
      throw new LocatedSiteCreationException(e.getMessage(), getPartialFilePath(), position);
    }
  }
}
