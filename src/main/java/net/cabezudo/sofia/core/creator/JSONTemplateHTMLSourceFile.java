package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.06
 */
class JSONTemplateHTMLSourceFile extends HTMLTemplateSourceFile {

  JSONTemplateHTMLSourceFile(Site site, Path commonsComponentsTemplatePath, Path voidTemplatePath, TemplateVariables templateVariables, Caller caller)
          throws IOException, SiteCreationException, LocatedSiteCreationException, SQLException, InvalidFragmentTag {
    super(site, commonsComponentsTemplatePath, voidTemplatePath, null, templateVariables, caller);
  }
}
