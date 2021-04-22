package net.cabezudo.sofia.geography;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.21
 */
public class State extends AdministrativeDivision {

  public State(int id, AdministrativeDivisionType type, String code, int fileId, AdministrativeDivision parent) {
    super(id, AdministrativeDivisionType.STATE, code, fileId, parent);
  }

}
