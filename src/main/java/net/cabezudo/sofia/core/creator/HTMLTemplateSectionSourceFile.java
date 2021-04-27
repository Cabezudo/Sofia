package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Path;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.06
 */
public class HTMLTemplateSectionSourceFile extends HTMLTemplateSourceFile {

  public HTMLTemplateSectionSourceFile(Site site, Path templatesBasePath, Path templatePath, String id, TemplateVariables templateVariables, TextsFile textsFile, Caller caller)
          throws IOException, SiteCreationException, LocatedSiteCreationException, InvalidFragmentTag {
    super(site, templatesBasePath, templatePath, id, templateVariables, textsFile, caller);
  }
}
