package net.cabezudo.sofia.core.catalogs;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.31
 */
public class CatalogEntry {

  private final int id;
  private final String name;

  public CatalogEntry(CatalogEntry entry) {
    this.id = entry.id;
    this.name = entry.name;
  }

  public CatalogEntry(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public JSONObject toJSONTree() {
    JSONObject jsonRestaurantType = new JSONObject();
    jsonRestaurantType.add(new JSONPair("id", id));
    jsonRestaurantType.add(new JSONPair("name", name));
    return jsonRestaurantType;
  }
}
