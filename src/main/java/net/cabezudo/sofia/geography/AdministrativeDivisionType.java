package net.cabezudo.sofia.geography;

import net.cabezudo.sofia.core.catalogs.CatalogEntry;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.21
 */
public class AdministrativeDivisionType extends CatalogEntry {

  public static final AdministrativeDivisionType CONTINENT = new AdministrativeDivisionType(1, "continent");
  public static final AdministrativeDivisionType COUNTRY = new AdministrativeDivisionType(2, "country");
  public static final AdministrativeDivisionType STATE = new AdministrativeDivisionType(3, "state");
  public static final AdministrativeDivisionType CITY = new AdministrativeDivisionType(4, "city");
  public static final AdministrativeDivisionType SETTLEMENT = new AdministrativeDivisionType(5, "settlement");

  public AdministrativeDivisionType(int id, String name) {
    super(id, name);
  }
}
