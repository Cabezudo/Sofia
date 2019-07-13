package net.cabezudo.sofia.core.creator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.SQLException;
import java.util.Set;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.core.files.FileHelper;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.users.authorization.AuthorizationManager;
import net.cabezudo.sofia.core.users.permission.PermissionTypeManager;
import net.cabezudo.sofia.core.users.profiles.PermissionType;
import net.cabezudo.sofia.core.users.profiles.ProfileManager;
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

  public void createPage(Site site, String requestURI) throws IOException, SQLException, FileNotFoundException, JSONParseException, SiteCreationException, InvalidFragmentTag {
    String htmlPartialPath = requestURI.substring(1);
    String voidPartialPath = requestURI.substring(1).substring(0, htmlPartialPath.length() - 5); // Used to create the javascript and css files for this html page

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
    templateLiterals.add(site.getSourcesPath(), "commons.json");
    String themeName = templateLiterals.get("themeName");
    if (themeName == null) {
      throw new SiteCreationException("Can't find the theme for the site in the commons.json file.");
    }

    // TODO Read all the theme style sheets after the entire site
    Path themeBasePath = Configuration.getInstance().getCommonsThemesPath().resolve(themeName);
    templateLiterals.add(themeBasePath, "values.json");

    Path styleFilePath = themeBasePath.resolve("style.css");
    CascadingStyleSheetsCode css = new CascadingStyleSheetsCode(templateLiterals);
    css.append(styleFilePath);

    try {
      FileHelper.copyDirectory(site.getSourcesImagesPath(), site.getImagesPath());
    } catch (FileNotFoundException e) {
      Logger.warning(e);
    }

    SofiaSourceFile sofiaSourceFile;
    try {
      sofiaSourceFile = new SofiaSourceFile(site, templateLiterals, voidPartialPath);
    } catch (NoSuchFileException e) {
      throw new SiteCreationException(e);
    }

    // TODO Borrar los permisos para esta pagina en este sitio en la base de datos.
    AuthorizationManager.getInstance().delete(requestURI, site);
    // Search and add permission for the page
    String profileString = sofiaSourceFile.getProfiles();
    Logger.debug("Profiles: " + profileString);
    if (profileString != null) {
      String[] ps = profileString.split(",");
      Profiles profiles = ProfileManager.getInstance().createFromNames(ps, site);
      PermissionType permissionType = PermissionTypeManager.getInstance().get("read", site);
      if (permissionType == null) {
        permissionType = PermissionTypeManager.getInstance().create("read", site);
      }
      AuthorizationManager.getInstance().add(profiles, requestURI, permissionType, site);
    }

    String html = sofiaSourceFile.getHTMLCode();
    try {
      html = templateLiterals.apply(html);
    } catch (UndefinedLiteralException e) {
      throw new SiteCreationException(e);
    }
    Files.write(fileContentPath, html.getBytes(StandardCharsets.UTF_8));

    JavaScriptCode js = new JavaScriptCode(templateLiterals);
    String templateLiteralsString = "const templateLiterals = " + templateLiterals.toJSON() + ";\n";

    js.append(templateLiteralsString);

    Files.walkFileTree(Configuration.getInstance().getCommonsLibsPath(), new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (file.toString().endsWith(".js")) {
          Logger.debug("Loading the file %s.", file);
          js.append(file);
        }
        return FileVisitResult.CONTINUE;
      }
    }
    );
    js.append(sofiaSourceFile.getJavaScriptCode());
    js.save(site, voidPartialPath);

    css.append(Configuration.getInstance().getCommonsLibsPath());
    css.append(sofiaSourceFile.getCascadingStyleSheetsCode());
    css.save(site, voidPartialPath);

    Set<Resource> resources = sofiaSourceFile.getResources();
    for (Resource resource : resources) {
      Path origin = resource.getOrigin();
      if (Files.exists(origin)) {
        Path target = resource.getTarget();
        if (Files.isDirectory(origin)) {
          Files.createDirectories(target);
          FileHelper.copyDirectory(origin, target);
        }
      } else {
        Logger.warning("Resource doesn't exist: %s", Configuration.getInstance().getSourcesPath().relativize(origin).toString());
      }
    }
  }
}
