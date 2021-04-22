package net.cabezudo.sofia.geography;

import net.cabezudo.sofia.names.InternationalizedNameManager;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.21
 */
public class AdministrativeDivisionNameManager extends InternationalizedNameManager {

  private static AdministrativeDivisionNameManager INSTANCE;

  public static AdministrativeDivisionNameManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new AdministrativeDivisionNameManager();
    }
    return INSTANCE;
  }

  public AdministrativeDivisionNameManager() {
    super(AdministrativeDivisionNameTable.DATABASE_NAME, AdministrativeDivisionNameTable.NAME);
  }
}
