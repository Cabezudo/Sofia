package net.cabezudo.sofia.states;

import net.cabezudo.sofia.countries.Country;
import net.cabezudo.sofia.geography.State;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class StateManager {

  private static StateManager INSTANCE;

  private StateManager() {
    // Protect de instance
  }

  public static StateManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new StateManager();
    }
    return INSTANCE;
  }

  public State add(Country country, String stateName) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
