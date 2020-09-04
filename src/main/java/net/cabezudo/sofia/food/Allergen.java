package net.cabezudo.sofia.food;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.food.helpers.AllergenHelper;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.02
 */
public class Allergen {

  private final int id;
  private final String name;

  Allergen(int id, String name) {
    this.id = id;
    this.name = name;
  }

  Allergen(AllergenHelper allergen) {
    this.id = allergen.getId();
    this.name = allergen.getName();
  }

  JSONValue toJSONTree() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.add(new JSONPair("id", id));
    jsonObject.add(new JSONPair("name", name));
    return jsonObject;
  }

}
