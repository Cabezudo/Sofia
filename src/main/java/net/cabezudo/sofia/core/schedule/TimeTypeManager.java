package net.cabezudo.sofia.core.schedule;

import java.sql.Connection;
import net.cabezudo.sofia.core.cache.Cache;
import net.cabezudo.sofia.core.cache.CacheManager;
import net.cabezudo.sofia.core.catalogs.CatalogEntry;
import net.cabezudo.sofia.core.catalogs.SimpleCatalogManager;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;

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
  public TimeType get(int id) throws ClusterException {
    return new TimeType(super.get(id));
  }

  @Override
  public TimeType get(String name) throws ClusterException {
    Cache<String, TimeType> cache = CacheManager.getInstance().getCache("TimeType");
    TimeType timeType = cache.get(name);
    if (timeType == null) {
      timeType = new TimeType(super.get(name));
      cache.put(name, timeType);
      return timeType;
    }
    return timeType;
  }

  @Override
  public TimeType get(Connection connection, String name) throws ClusterException {
    CatalogEntry catalogEntry = super.get(connection, name);
    if (catalogEntry == null) {
      throw new SofiaRuntimeException("Invalid time type '" + name + "'. Time type not found in database.");
    }
    return new TimeType(catalogEntry);
  }
}
