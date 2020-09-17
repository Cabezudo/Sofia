package net.cabezudo.sofia.restaurants;

import net.cabezudo.sofia.core.catalogs.CatalogEntry;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.29
 */
public class RestaurantType extends CatalogEntry {

  public RestaurantType(int id, String name) {
    super(id, name);
  }

  RestaurantType(CatalogEntry entry) {
    super(entry);
  }
}
