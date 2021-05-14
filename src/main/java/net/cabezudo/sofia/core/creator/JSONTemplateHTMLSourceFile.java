package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Path;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.06
 */
class JSONTemplateHTMLSourceFile extends HTMLTemplateSourceFile {

  JSONTemplateHTMLSourceFile(Site site, Path commonsHTMLTemplatesPath, Path voidTemplatePath, TemplateVariables templateVariables, TextsFile textsFile, Caller caller)
          throws IOException, SiteCreationException, LocatedSiteCreationException, InvalidFragmentTag {
    super(site, commonsHTMLTemplatesPath, voidTemplatePath, null, templateVariables, textsFile, caller);
  }
}
