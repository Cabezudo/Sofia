package net.cabezudo.sofia.core.schedule;

import java.sql.Connection;
import java.sql.SQLException;
import net.cabezudo.sofia.core.catalogs.SimpleCatalogManager;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.31
 */
public class TimeTypeManager extends SimpleCatalogManager<TimeType> {

  private static final TimeTypeManager INSTANCE = new TimeTypeManager();

  private TimeTypeManager() {
    super(TimeTypesTable.DATABASE, TimeTypesTable.NAME);
  }

  public static TimeTypeManager getInstance() {
    return INSTANCE;
  }

  @Override
  public TimeType get(int id) throws SQLException {
    return new TimeType(super.get(id));
  }

  @Override
  public TimeType get(String name) throws SQLException {
    // TODO add a cache here
    return new TimeType(super.get(name));
  }

  @Override
  public TimeType get(Connection connection, String name) throws SQLException {
    return new TimeType(super.get(connection, name));
  }
}
