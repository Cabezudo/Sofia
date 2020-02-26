package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.12.04
 */
class HTMLFragmentSourceFile extends HTMLSourceFile {

  HTMLFragmentSourceFile(Site site, Path basePath, Path partialPath, TemplateVariables templateVariables, Caller caller) throws IOException, LocatedSiteCreationException, SiteCreationException, SQLException, InvalidFragmentTag {
    super(site, basePath, partialPath, templateVariables, caller);
  }

  @Override
  public boolean searchHTMLTag(SofiaSource actual, String line, int lineNumber) throws SQLException, InvalidFragmentTag {
    if (line.startsWith("<html")) {
      throw new InvalidFragmentTag("A HTML fragment can't have the <html> tag", 0);
    }
    return false;
  }
}
