package net.cabezudo.sofia.geography;

import net.cabezudo.sofia.core.languages.Language;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.07
 */
public abstract class AdministrativeDivisionType {

  public static final int BOROUGH = 1;
  public static final int CANTON = 2;
  public static final int CITY = 3;
  public static final int CPIMTU = 4;
  public static final int DEPARTMENT = 5;
  public static final int DISTRICT = 6;
  public static final int EMIRATE = 7;
  public static final int STATE = 8;
  public static final int GOVERNORATE = 9;
  public static final int HUNDRED = 10;
  public static final int METROPOLITAN_AREA = 11;
  public static final int MUNICIPALITY = 12;
  public static final int PARISH = 13;
  public static final int PREFECTURE = 14;
  public static final int PROVINCE = 15;
  public static final int REGION = 16;
  public static final int RURAL_DISTRICT = 17;
  public static final int SHIRE = 18;
  public static final int SUBDISTRICT = 19;
  public static final int SUBPREFECTURE = 20;
  public static final int TOWN = 21;
  public static final int TOWNSHIP = 22;
  public static final int VILLAGE = 23;

  private final String name;

  public AdministrativeDivisionType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public String getName(Language language) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
