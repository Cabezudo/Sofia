package net.cabezudo.sofia.geography;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.21
 */
public class City extends AdministrativeDivision {

  public City(int id, String code, int fileId, AdministrativeDivision parent) {
    super(id, AdministrativeDivisionType.CITY, code, fileId, parent);
  }

}
