package net.cabezudo.sofia.core.creator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.core.files.FileHelper;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.users.authorization.AuthorizationManager;
import net.cabezudo.sofia.core.users.permission.PermissionTypeManager;
import net.cabezudo.sofia.core.users.profiles.PermissionType;
import net.cabezudo.sofia.core.users.profiles.Profiles;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.23
 */
public class SiteCreator {

  private static SiteCreator INSTANCE;

  private SiteCreator() {
    // To protect the instance;
  }

  public static SiteCreator getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new SiteCreator();
    }
    return INSTANCE;
  }

  public void createPage(Site site, String requestURI) throws IOException, SQLException, FileNotFoundException, JSONParseException, SiteCreationException, InvalidFragmentTag, LibraryVersionException {
    String htmlPartialPath = requestURI.substring(1);
    String voidPartialPathName = requestURI.substring(1).substring(0, htmlPartialPath.length() - 5); // Used to create the javascript and css files for this html page
    Path voidPartialPath = Paths.get(voidPartialPathName);

    Path fileContentPath = site.getVersionPath().resolve(htmlPartialPath);
    if (Environment.getInstance().isProduction() && Files.exists(fileContentPath)) {
      return;
    }
    Files.createDirectories(fileContentPath.getParent());
    Files.createDirectories(site.getBasePath());
    Files.createDirectories(site.getVersionPath());
    Files.createDirectories(site.getJSPath());
    Files.createDirectories(site.getCSSPath());
    Files.createDirectories(site.getImagesPath());

    TemplateVariables templateVariables = new TemplateVariables(site, voidPartialPathName);
    try {
      templateVariables.add(site.getSourcesPath(), "commons.json");
    } catch (UndefinedLiteralException e) {
      throw new SiteCreationException(e.getMessage());
    }
    String themeName = templateVariables.get("themeName");
    if (themeName == null) {
      throw new SiteCreationException("Can't find the theme for the site in the commons.json file.");
    }

    // TODO Read all the theme style sheets after the entire site
    ThemeSourceFile themeSourceFile = new ThemeSourceFile(themeName, templateVariables);

    try {
      FileHelper.copyDirectory(site.getSourcesImagesPath(), site.getImagesPath());
    } catch (FileNotFoundException e) {
      Logger.warning("Image directory not found: %s", site.getSourcesImagesPath());
    }

    BaseHTMLSourceFile baseHTLMSourceFile = new BaseHTMLSourceFile(site, templateVariables, voidPartialPath);

    // sofiaSources.create();
    //    createPagePermissions(site, sofiaSources, requestURI);
    //
    //    if (Environment.getInstance().isDevelopment()) {
    //      Logger.debug(templateVariables.toJSON());
    //    }
    Path cascadingStyleSheetPath = Paths.get(voidPartialPath + ".css");
    CascadingStyleSheetSourceFile css = new CascadingStyleSheetSourceFile(site.getCSSPath(), cascadingStyleSheetPath);
    css.add(site.getSourcesPath(), "fonts.css", templateVariables, null);
    css.add(site.getSourcesPath(), "style.css", templateVariables, null);
    css.add(themeSourceFile);
    css.add(baseHTLMSourceFile);
    css.save();

    templateVariables.save();

    baseHTLMSourceFile.save();
  }

  private void createPagePermissions(Site site, SofiaSources sofiaSourceFile, String requestURI) throws SQLException {
    // TODO Borrar los permisos para esta pagina en este sitio en la base de datos.
    AuthorizationManager.getInstance().delete(requestURI, site);
    // Search and add permission for the page
    Profiles profiles = sofiaSourceFile.getProfiles();
    if (!profiles.isEmpty()) {
      PermissionType permissionType = PermissionTypeManager.getInstance().get("read", site);
      if (permissionType == null) {
        permissionType = PermissionTypeManager.getInstance().create("read", site);
      }
      AuthorizationManager.getInstance().add(profiles, requestURI, permissionType, site);
    }
  }

  private void save(String html, Path filePath) throws SiteCreationException, IOException {
    Logger.debug("Creating the file %s.", filePath);
    Files.write(filePath, html.getBytes(Configuration.getInstance().getEncoding()));
  }
}
