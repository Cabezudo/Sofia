package net.cabezudo.sofia.core.schedule;

import net.cabezudo.sofia.core.catalogs.CatalogEntry;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.29
 */
public class TimeType extends CatalogEntry {

  public TimeType(int id, String name) {
    super(id, name);
  }

  public TimeType(CatalogEntry entry) {
    super(entry);
  }
}
