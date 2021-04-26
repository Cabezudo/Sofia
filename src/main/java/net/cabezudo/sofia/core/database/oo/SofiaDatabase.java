package net.cabezudo.sofia.core.database.oo;

import java.io.IOException;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.sofia.core.cluster.ClusterException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.03.30
 */
public class SofiaDatabase {

  private static final SofiaDatabase INSTANCE = new SofiaDatabase();

  public static SofiaDatabase getInstance() {
    return INSTANCE;
  }

  public void loadData() throws JSONParseException, IOException, ClusterException, PropertyNotExistException {
    // Load data for Sofia Object Oriented Database
  }

}
