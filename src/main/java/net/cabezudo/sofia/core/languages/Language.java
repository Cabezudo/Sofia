package net.cabezudo.sofia.core.languages;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.11
 */
public class Language {

  private final int id;
  private final String code;

  public Language(int id, String code) {
    this.id = id;
    this.code = code;
  }

  public int getId() {
    return id;
  }

  public String getTwoLettersCode() {
    return code;
  }

  public JSONObject toJSONTree() {
    JSONObject jsonRestaurantType = new JSONObject();
    jsonRestaurantType.add(new JSONPair("id", id));
    jsonRestaurantType.add(new JSONPair("code", code));
    return jsonRestaurantType;
  }
}
