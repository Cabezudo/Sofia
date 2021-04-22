package net.cabezudo.sofia.geography;

import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.20
 */
public class AdministrativeDivisionTypeManager {

  private static AdministrativeDivisionTypeManager INSTANCE;

  public static AdministrativeDivisionTypeManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new AdministrativeDivisionTypeManager();
    }
    return INSTANCE;
  }

  private AdministrativeDivisionTypeManager() {
  }

  public AdministrativeDivisionType get(String name) {
    // TODO add to database
    switch (name) {
      case "continent":
        return AdministrativeDivisionType.CONTINENT;
      case "country":
        return AdministrativeDivisionType.COUNTRY;
      case "state":
        return AdministrativeDivisionType.STATE;
      case "city":
        return AdministrativeDivisionType.CITY;
      case "settlement":
        return AdministrativeDivisionType.SETTLEMENT;
      default:
        throw new SofiaRuntimeException("Invalid administrative división type name: " + name);
    }
  }

  public AdministrativeDivisionType get(int id) {
    // TODO add to database
    switch (id) {
      case 1:
        return AdministrativeDivisionType.CONTINENT;
      case 2:
        return AdministrativeDivisionType.COUNTRY;
      case 3:
        return AdministrativeDivisionType.STATE;
      case 4:
        return AdministrativeDivisionType.CITY;
      case 5:
        return AdministrativeDivisionType.SETTLEMENT;
      default:
        throw new SofiaRuntimeException("Invalid administrative división type name: " + id);
    }
  }
}
