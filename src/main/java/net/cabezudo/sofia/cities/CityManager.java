package net.cabezudo.sofia.cities;

import java.sql.Connection;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.geography.City;
import net.cabezudo.sofia.geography.State;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class CityManager {

  private static CityManager INSTANCE;

  private CityManager() {
    // Protecte the instance
  }

  public static CityManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new CityManager();
    }
    return INSTANCE;
  }

  public City get(Connection connection, State state, String cityName, User owner) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
