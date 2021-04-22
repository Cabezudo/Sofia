package net.cabezudo.sofia.geography;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.21
 */
public class Settlement extends AdministrativeDivision {

  public Settlement(int id, String code, int fileId, AdministrativeDivision parent) {
    super(id, AdministrativeDivisionType.SETTLEMENT, code, fileId, parent);
  }

}
