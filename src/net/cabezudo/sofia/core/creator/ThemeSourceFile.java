package net.cabezudo.sofia.core.creator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.26
 */
final class ThemeSourceFile extends SofiaSourceFile {

  private List<CSSSourceFile> files = new ArrayList<>();
  private String themeName;
  private final CSSSourceFile css;

  ThemeSourceFile(Site site, String themeName, TemplateVariables templateVariables) throws IOException, SiteCreationException {
    super(site, Configuration.getInstance().getCommonsThemesPath(), Paths.get(themeName), templateVariables, null);
    this.themeName = themeName;

    Path themeBasePath = Configuration.getInstance().getCommonsThemesPath().resolve(themeName);
    try {
      templateVariables.add(themeBasePath, "values.json");
    } catch (UndefinedLiteralException | JSONParseException | FileNotFoundException e) {
      throw new SiteCreationException(e.getMessage());
    }
    css = new CSSSourceFile(getSite(), themeBasePath, Paths.get("style.css"), templateVariables, null);
  }

  @Override
  public void add(Line line) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void add(Lines lines) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String getVoidPartialPathName() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
