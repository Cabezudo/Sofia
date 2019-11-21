package net.cabezudo.sofia.core.creator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.sql.SQLException;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.users.profiles.Profiles;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.23
 */
public class SofiaSources {

  private final Site site;
  private final TemplateLiterals templateLiterals;
  private final HTMLSourcesContainer html;
  private final JavaScriptSourcesContainer js;
  private final CascadingStyleSheetSourcesContainer css;
  private final Path voidPartialPath;

  SofiaSources(Site site, TemplateLiterals templateLiterals, Path voidPartialPath) throws IOException, SiteCreationException, LibraryVersionException, SQLException {
    this.site = site;
    this.templateLiterals = templateLiterals;
    this.voidPartialPath = voidPartialPath;
    html = new HTMLSourcesContainer(site, templateLiterals);
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
    String templateLiteralsString = "const templateLiterals = " + templateLiterals.toJSON() + ";\n";
    JavaScriptSourcesContainer variablesFile = new JavaScriptSourcesContainer();
    Path variablesFilePath = site.getJSPath().resolve("variables.js");
    variablesFile.setTargetFilePath(variablesFilePath);

    SofiaJavaScriptSource variablesSource = new SofiaJavaScriptSource(null);
    variablesSource.add(new CodeLine(templateLiteralsString, null, 0));
    variablesFile.add(variablesSource);

    Libraries libraries = html.getLibraries();
    css.add(libraries);
    js.add(libraries);
    SofiaCascadingStyleSheetSource sofiaCascadingStyleSheetSource = html.getSofiaCascadingStyleSheetSource();
    css.add(sofiaCascadingStyleSheetSource);
    SofiaJavaScriptSource sofiaJavaScriptSource = html.getSofiaJavaScriptSource();
    js.add(sofiaJavaScriptSource);

    try {
      variablesFile.apply(templateLiterals);
      html.apply(templateLiterals);
      css.apply(templateLiterals);
      js.apply(templateLiterals);
    } catch (UndefinedLiteralException e) {
      throw new LocatedSiteCreationException(e.getMessage(), e.getFilePath(), e.getPosition());
    }

    // TODO add libraries to js file
    variablesFile.save();
    html.save();
    css.save();
    js.save();
  }

  Profiles getProfiles() {
    return html.getProfiles();
  }
}
