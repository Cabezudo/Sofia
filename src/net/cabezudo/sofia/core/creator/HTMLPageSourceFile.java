package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.users.profiles.ProfileManager;
import net.cabezudo.sofia.core.users.profiles.Profiles;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.06
 */
class HTMLPageSourceFile extends HTMLSourceFile {

  HTMLPageSourceFile(Site site, Path basePath, Path htmlPartialPath, TemplateVariables templateVariables, Caller caller)
          throws IOException, SiteCreationException, LocatedSiteCreationException, SQLException, InvalidFragmentTag {
    super(site, basePath, htmlPartialPath, null, templateVariables, caller);
  }

  @Override
  public boolean searchHTMLTag(SofiaSource actual, String line, Path filePath, int lineNumber) throws SQLException, InvalidFragmentTag {
    if (line.startsWith("<html")) {
      int i = line.indexOf("profiles");
      if (i > 0) {
        String profileString = line.substring(i + 10, line.length() - 2);
        Logger.debug("Profiles: " + profileString);
        String[] ps = profileString.split(",");
        Profiles profiles = ProfileManager.getInstance().createFromNames(ps, getSite());
        setProfiles(profiles);
      }
      add(new CodeLine("<html>\n", lineNumber));
      return true;
    }
    return false;
  }

  @Override
  Path getSourceFilePath(Caller caller) throws SiteCreationException {
    Path htmlSourceFilePath;
    if (getPartialFilePath().startsWith("/")) {
      htmlSourceFilePath = Configuration.getInstance().getCommonsComponentsTemplatesPath().resolve(getPartialFilePath().toString().substring(1));
    } else {
      htmlSourceFilePath = Configuration.getInstance().getCommonsComponentsTemplatesPath().resolve(getPartialFilePath().toString());
    }
    Logger.debug("HTMLPageSourceFile:getSourceFilePath:Load HTML source file %s.", htmlSourceFilePath);

    if (Files.exists(htmlSourceFilePath)) {
      return htmlSourceFilePath;
    }
    if (getPartialFilePath().startsWith("/")) {
      htmlSourceFilePath = getSite().getVersionedSourcesPath().resolve(getPartialFilePath().toString().substring(1));
    } else {
      htmlSourceFilePath = getSite().getVersionedSourcesPath().resolve(getPartialFilePath());
    }
    if (Files.exists(htmlSourceFilePath)) {
      return htmlSourceFilePath;
    }
    Logger.debug("HTMLPageSourceFile:getSourceFilePath:htmlSourceFilePath: %s", htmlSourceFilePath);
    throw new SiteCreationException("The file " + getPartialFilePath() + " called on " + caller + " NOT FOUND");
  }

  @Override
  String replaceTemplateVariables(String line, int lineNumber, Path htmlSourceFilePath) throws LocatedSiteCreationException {
    try {
      return getTemplateVariables().replace(line, lineNumber, htmlSourceFilePath);
    } catch (UndefinedLiteralException e) {
      Position position = new Position(lineNumber, e.getRow());
      throw new LocatedSiteCreationException(e.getMessage(), getPartialFilePath(), position);
    }
  }
}
