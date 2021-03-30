package net.cabezudo.sofia.core.database.sql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.cabezudo.sofia.core.configuration.DataCreator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.10.12
 */
public class DatabaseCreators implements Iterable<DataCreator> {

  private final List<DataCreator> list = new ArrayList<>();

  @Override
  public Iterator<DataCreator> iterator() {
    return list.iterator();
  }

  public void add(DataCreator databaseCreator) {
    list.add(databaseCreator);
  }

}
