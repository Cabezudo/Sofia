package net.cabezudo.sofia.core.sites.validators;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.05.01
 */
public class SiteVersionValidator {

  private static SiteVersionValidator INSTANCE;

  public static SiteVersionValidator getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new SiteVersionValidator();
    }
    return INSTANCE;
  }

  public void validate(String stringVersion) throws SiteVersionException {
    if (stringVersion == null || stringVersion.isBlank()) {
      throw new SiteVersionException("site.version.empty");
    }
    double doubleVersion;
    try {
      doubleVersion = Double.parseDouble(stringVersion);
    } catch (NumberFormatException e) {
      throw new SiteVersionException("site.version.invalid", stringVersion);
    }
    if (doubleVersion == 0) {
      throw new SiteVersionException("site.version.zero");
    }
    if (doubleVersion < 0) {
      throw new SiteVersionException("site.version.negative");
    }
    int version = (int) doubleVersion;
    if (version != doubleVersion) {
      throw new SiteVersionException("site.version.integer");
    }
  }
}
