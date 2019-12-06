package net.cabezudo.sofia.core.creator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.sql.SQLException;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.users.profiles.Profiles;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.23
 */
public class SofiaSources {

  private final Site site;
  private final TemplateVariables templateVariables;
  private final HTMLSourcesContainer html;
  private final JavaScriptSourcesContainer js;
  private final CascadingStyleSheetSourcesContainer css;
  private final Path voidPartialPath;

  SofiaSources(Site site, TemplateVariables templateVariables, Path voidPartialPath) throws IOException, SiteCreationException, LibraryVersionException, SQLException {
    this.site = site;
    this.templateVariables = templateVariables;
    this.voidPartialPath = voidPartialPath;
    html = new HTMLSourcesContainer(site, templateVariables);
    Path htmlPath = site.getVersionPath().resolve(voidPartialPath + ".html");
    html.setTargetFilePath(htmlPath);

    js = new JavaScriptSourcesContainer();
    Path javaScriptPath = site.getJSPath().resolve(voidPartialPath + ".js");
    js.setTargetFilePath(javaScriptPath);

    css = new CascadingStyleSheetSourcesContainer();
    Path cascadingStyleSheetPath = site.getCSSPath().resolve(voidPartialPath + ".css");
    css.setTargetFilePath(cascadingStyleSheetPath);

    css.load(site.getSourcesPath(), "fonts.css", null); // CSS with fonts
  }

  void setTheme(Path themeBasePath) throws SiteCreationException, IOException, LibraryVersionException, SQLException {
    css.load(themeBasePath, "style.css", null); // CSS from themes
  }

  void create() throws SiteCreationException, IOException, LibraryVersionException, NoSuchFileException, FileNotFoundException, SQLException {
    html.load(site.getSourcesPath(), voidPartialPath);

    // TODO agregar las variables al inicio del archivo de JS
    // TODO apply here the template literals
    // TODO load here, at first, the js libraries from the sources, and the css, and the images, and videos and sounds
    String templateVariablesString = "const templateVariables = " + templateVariables.toJSON() + ";\n";
    JavaScriptSourcesContainer templateVariablesFile = new JavaScriptSourcesContainer();
    Path variablesFilePath = site.getJSPath().resolve(voidPartialPath + "TemplateVariables.js");
    templateVariablesFile.setTargetFilePath(variablesFilePath);

    SofiaJavaScriptSource templateVariablesSource = new SofiaJavaScriptSource(null);
    templateVariablesSource.add(new CodeLine(templateVariablesString, 0));
    templateVariablesFile.add(templateVariablesSource);

    Libraries libraries = html.getLibraries();
    css.add(libraries);
    js.add(libraries);
    SofiaCascadingStyleSheetSource sofiaCascadingStyleSheetSource = html.getSofiaCascadingStyleSheetSource();
    css.add(sofiaCascadingStyleSheetSource);
    SofiaJavaScriptSource sofiaJavaScriptSource = html.getSofiaJavaScriptSource();
    js.add(sofiaJavaScriptSource);

    if (Environment.getInstance().isDevelopment()) {
      Logger.debug(templateVariablesString);
    }

    // TODO add libraries to js file
    templateVariablesFile.save();
    html.save();
    css.save();
    js.save();
  }

  Profiles getProfiles() {
    return html.getProfiles();
  }
}
