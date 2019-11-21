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

    TemplateLiterals templateLiterals = new TemplateLiterals();
    try {
      templateLiterals.add(site.getSourcesPath(), "commons.json");
    } catch (UndefinedLiteralException e) {
      throw new SiteCreationException(e.getMessage());
    }
    String themeName = templateLiterals.get("themeName");
    if (themeName == null) {
      throw new SiteCreationException("Can't find the theme for the site in the commons.json file.");
    }

    SofiaSources sofiaSources = new SofiaSources(site, templateLiterals, voidPartialPath);
    // TODO Read all the theme style sheets after the entire site
    Path themeBasePath = Configuration.getInstance().getCommonsThemesPath().resolve(themeName);
    try {
      templateLiterals.add(themeBasePath, "values.json");
    } catch (UndefinedLiteralException e) {
      throw new SiteCreationException(e.getMessage());
    }
    sofiaSources.setTheme(themeBasePath);

    try {
      FileHelper.copyDirectory(site.getSourcesImagesPath(), site.getImagesPath());
    } catch (FileNotFoundException e) {
      Logger.warning("Image directory not found: %s", site.getSourcesImagesPath());
    }

    sofiaSources.create();

    createPagePermissions(site, sofiaSources, requestURI);

    if (Environment.getInstance().isDevelopment()) {
      Logger.debug(templateLiterals.toJSON());
    }
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
}
