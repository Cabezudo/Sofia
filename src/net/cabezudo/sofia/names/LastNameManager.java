package net.cabezudo.sofia.names;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.16
 */
public class LastNameManager {

  private static LastNameManager INSTANCE;

  private LastNameManager() {
    // Nothing to do here
  }

  public static LastNameManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new LastNameManager();
    }
    return INSTANCE;
  }

  public String validate(String lastName) {
    return "lastName.ok";
  }
}
