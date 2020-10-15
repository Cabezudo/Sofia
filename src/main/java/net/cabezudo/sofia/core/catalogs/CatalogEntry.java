package net.cabezudo.sofia.core.catalogs;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.31
 */
public class CatalogEntry {

  private final int id;
  private final String name;

  public CatalogEntry(CatalogEntry entry) {
    if (entry == null) {
      throw new SofiaRuntimeException("null parameter");
    }
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

  @Override
  public String toString() {
    return "[ " + id + ", " + name + " ]";
  }

  public JSONObject toJSONTree() {
    JSONObject jsonRestaurantType = new JSONObject();
    jsonRestaurantType.add(new JSONPair("id", id));
    jsonRestaurantType.add(new JSONPair("name", name));
    return jsonRestaurantType;
  }

  public String toJSON() {
    return toJSONTree().toString();
  }
}
