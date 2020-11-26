package net.cabezudo.sofia.core.catalogs;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.languages.Language;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.31
 */
public class InternationalizedCatalogEntry {

  private final int id;
  private final Language language;
  private final String name;

  public InternationalizedCatalogEntry(InternationalizedCatalogEntry entry) {
    if (entry == null) {
      throw new SofiaRuntimeException("null parameter");
    }
    this.id = entry.id;
    this.language = entry.language;
    this.name = entry.name;
  }

  public InternationalizedCatalogEntry(int id, Language language, String name) {
    this.id = id;
    this.language = language;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public Language getLanguage() {
    return language;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "[ " + id + ", " + language + ", " + name + " ]";
  }

  public JSONObject toJSONTree() {
    JSONObject jsonRestaurantType = new JSONObject();
    jsonRestaurantType.add(new JSONPair("id", id));
    jsonRestaurantType.add(new JSONPair("language", language.toJSONTree()));
    jsonRestaurantType.add(new JSONPair("name", name));
    return jsonRestaurantType;
  }

  public String toJSON() {
    return toJSONTree().toString();
  }
}
