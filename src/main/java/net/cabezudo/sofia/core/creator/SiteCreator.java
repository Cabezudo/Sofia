package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.users.authorization.AuthorizationManager;
import net.cabezudo.sofia.core.users.permission.PermissionTypeManager;
import net.cabezudo.sofia.core.users.profiles.PermissionType;
import net.cabezudo.sofia.core.users.profiles.Profiles;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.23
 */
public class SiteCreator {

  private static final SiteCreator INSTANCE = new SiteCreator();

  private SiteCreator() {
  }

  public static SiteCreator getInstance() {
    return INSTANCE;
  }

  public void createPages(Site site, String requestURI) throws IOException, SQLException, JSONParseException, SiteCreationException, LocatedSiteCreationException, InvalidFragmentTag, LibraryVersionConflictException {
    String htmlPartialPathName = requestURI.substring(1);
    String voidPartialPathName = requestURI.substring(1).substring(0, htmlPartialPathName.length() - 5); // Used to create the javascript and css files for this html page
    Path htmlPartialPath = Paths.get(voidPartialPathName + ".html");
    Path cssPartialPath = Paths.get(voidPartialPathName + ".css");
    Path jsPartialPath = Paths.get(voidPartialPathName + ".js");

    Path fileContentPath = site.getVersionPath().resolve(htmlPartialPath);
    if (Environment.getInstance().isProduction() && Files.exists(fileContentPath)) {
      return;
    }
    Files.createDirectories(fileContentPath.getParent());
    Files.createDirectories(site.getBasePath());
    Files.createDirectories(site.getVersionPath());

    TemplateVariables templateVariables = new TemplateVariables();
    try {
      templateVariables.add(site.getVersionedSourcesPath(), Site.COMMONS_FILE_NAME);
    } catch (UndefinedLiteralException e) {
      throw new SiteCreationException(e.getMessage());
    }
    String themeName = templateVariables.get("themeName");
    if (themeName == null) {
      throw new SiteCreationException("Can't find the theme for the site in the " + Site.COMMONS_FILE_NAME + " file.");
    }

    // TODO Read all the theme style sheets after the entire site
    ThemeSourceFile themeSourceFile = new ThemeSourceFile(site, themeName, templateVariables);
    themeSourceFile.loadFile();

    Path basePath = site.getVersionedSourcesPath();

    HTMLSourceFile baseFile = new HTMLPageSourceFile(site, basePath, htmlPartialPath, templateVariables, null);
    baseFile.loadHTMLFile();

    Caller baseFileCaller = new Caller(baseFile, 0);
    createPagePermissions(site, baseFile, requestURI);

    JSSourceFile jsFile = new JSSourceFile(site, basePath, jsPartialPath, templateVariables, baseFileCaller);
    jsFile.add(baseFile.getLibraries());
    jsFile.add(baseFile.getJavaScriptLines());
    Path jsFilePath = site.getFilesPath(jsPartialPath);
    jsFile.save(jsFilePath);

    CSSSourceFile cssFile = new CSSSourceFile(site, basePath, cssPartialPath, templateVariables, baseFileCaller);
    cssFile.add(themeSourceFile.getCascadingStyleSheetImports());
    cssFile.add(themeSourceFile.getCascadingStyleSheetLines());
    cssFile.add(baseFile.getLibraries());
    cssFile.add(baseFile.getCascadingStyleSheetImports());
    cssFile.add(baseFile.getCascadingStyleSheetLines());
    // Read all the CSS files from the CSS path
    Path cssFilePath = site.getFilesPath(cssPartialPath);
    cssFile.save(cssFilePath);

    Path htmlFilePath = site.getVersionPath().resolve(htmlPartialPath);
    baseFile.save(htmlFilePath);
  }

  private void createPagePermissions(Site site, HTMLSourceFile htmlSourceFile, String requestURI) throws SQLException {
    Logger.debug("Create permission for request %s of site %s.", requestURI, site.getId());
    // TODO Borrar los permisos para esta pagina en este sitio en la base de datos.
    AuthorizationManager.getInstance().delete(requestURI, site);
    // Search and add permission for the page
    Profiles profiles = htmlSourceFile.getProfiles();
    if (!profiles.isEmpty()) {
      PermissionType permissionType = PermissionTypeManager.getInstance().get("read", site);
      if (permissionType == null) {
        permissionType = PermissionTypeManager.getInstance().create("read", site);
      }
      AuthorizationManager.getInstance().add(profiles, requestURI, permissionType, site);
    } else {
      Logger.debug("Profiles is empty for request %s of site %s.", requestURI, site.getId());
    }
  }
}
