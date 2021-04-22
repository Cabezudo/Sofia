package net.cabezudo.sofia.municipalities;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class MunicipalityManager {

  private static MunicipalityManager instance;

  public static MunicipalityManager getInstance() {
    if (instance == null) {
      instance = new MunicipalityManager();
    }
    return instance;
  }
}
