package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.cabezudo.json.JSON;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.core.files.FileHelper;
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

  public void createPages(Site site, String requestURI)
          throws IOException, JSONParseException, SiteCreationException, LocatedSiteCreationException, InvalidFragmentTag, LibraryVersionConflictException, ClusterException {

    Logger.debug("Create pages for URI %s.", requestURI);
    String htmlPartialPathName = requestURI.substring(1);
    String voidPartialPathName = FileHelper.removeExtension(htmlPartialPathName); // Used to create the javascript and css files for this html page
    Path htmlPartialPath = Paths.get(voidPartialPathName + ".html");
    Path htmlFilePath = site.getVersionedBasePath().resolve(htmlPartialPath);
    if (Environment.getInstance().isProduction() && Files.exists(htmlFilePath)) {
      return;
    }

    Path cssPartialPath = Paths.get(voidPartialPathName + ".css");
    Path jsPartialPath = Paths.get(voidPartialPathName + ".js");
    Path textsPartialPath = Paths.get(voidPartialPathName);

    Path versionPath = site.getVersionedBasePath();
    Path fileContentPath = versionPath.resolve(htmlPartialPath);
    if (Environment.getInstance().isProduction() && Files.exists(fileContentPath)) {
      return;
    }
    Files.createDirectories(fileContentPath.getParent());
    Files.createDirectories(site.getFullBasePath());
    Files.createDirectories(site.getVersionedBasePath());
    Files.createDirectories(site.getCustomVersionedImagesPath());
    Files.createDirectories(site.getCustomFilesPath());

    TemplateVariables templateVariables = new TemplateVariables();
    TextsFile textsFile = new TextsFile();

    try {
      templateVariables.add(site.getVersionedSourcesPath(), Site.COMMONS_CONFIGURATION_FILE_NAME);
    } catch (UndefinedLiteralException e) {
      throw new SiteCreationException(e.getMessage());
    }

    ThemeSourceFile themeSourceFile = null;
    String themeName = templateVariables.get("themeName");
    if (themeName != null) {
      // TODO Read all the theme style sheets after the entire site
      themeSourceFile = new ThemeSourceFile(site, themeName, templateVariables);
      themeSourceFile.loadFile();
    }

    Path basePath = site.getVersionedSourcesPath();

    HTMLSourceFile baseFile = new HTMLPageSourceFile(site, basePath, htmlPartialPath, templateVariables, textsFile, null);
    baseFile.loadHTMLFile();

    Caller caller = new Caller(baseFile, 0);
    createPagePermissions(site, baseFile, requestURI);

    JSSourceFile jsFile = new JSSourceFile(site, basePath, jsPartialPath, templateVariables, caller);
    jsFile.add(baseFile.getLibraries());
    jsFile.add(baseFile.getJavaScriptLines());
    Path jsFilePath = site.getCreatedFilesPath(jsPartialPath);
    jsFile.save(jsFilePath);

    CSSSourceFile cssFile = new CSSSourceFile(site, basePath, cssPartialPath, templateVariables, caller);
    if (themeSourceFile != null) {
      cssFile.add(themeSourceFile.getCascadingStyleSheetImports());
      cssFile.add(themeSourceFile.getCascadingStyleSheetLines());
    }

    cssFile.load(site.getVersionedSourcesPath(), Site.COMMONS_CSS_FILE_NAME, caller);

    cssFile.add(baseFile.getLibraries());
    cssFile.add(baseFile.getCascadingStyleSheetImports());
    cssFile.add(baseFile.getCascadingStyleSheetLines());
    // Read all the CSS files from the CSS path
    Path cssFilePath = site.getCreatedFilesPath(cssPartialPath);
    cssFile.save(cssFilePath);

    Path commonsFileTextsPath = site.getVersionedSourcesPath().resolve(Site.TEXTS_FILE_NAME);
    try {
      JSONObject jsonTexts = JSON.parse(commonsFileTextsPath, Configuration.getDefaultCharset()).toJSONObject();
      Logger.debug("Trying to load common text file %s.", commonsFileTextsPath);
      textsFile.add(jsonTexts);
    } catch (NoSuchFileException nsfe) {
      Logger.debug("Common texts file %s NOT FOUND.", commonsFileTextsPath);
    }

    Path textsPageFilePath = site.getVersionedSourcesPath().resolve(textsPartialPath + ".texts.json");
    try {
      Logger.debug("Trying to load the page text file %s.", textsPageFilePath);
      JSONObject jsonTexts = JSON.parse(textsPageFilePath, Configuration.getDefaultCharset()).toJSONObject();
      textsFile.add(jsonTexts);
    } catch (NoSuchFileException nsfe) {
      Logger.debug("Common texts file %s NOT FOUND.", textsPageFilePath);
    }

    textsFile.add(baseFile.getLibraries());
    Path textsFilePath = site.getCreatedFilesPath(textsPartialPath);
    textsFile.save(textsFilePath);

    baseFile.save(htmlFilePath);
  }

  private void createPagePermissions(Site site, HTMLSourceFile htmlSourceFile, String requestURI) throws ClusterException {
    Logger.debug("Create permission for request %s of site %s.", requestURI, site.getId());
    // TODO Borrar los permisos para esta pagina en este sitio en la base de datos.
    // TODO agregar a los permisos la companía
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
