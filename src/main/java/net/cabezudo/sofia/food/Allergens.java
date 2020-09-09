package net.cabezudo.sofia.food;

import java.util.ArrayList;
import java.util.List;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.sofia.food.helpers.AllergenHelper;
import net.cabezudo.sofia.food.helpers.AllergensHelper;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.02
 */
public final class Allergens {

  private final List<Allergen> list = new ArrayList<>();

  Allergens(AllergensHelper allergens) {
    for (AllergenHelper allergen : allergens) {
      add(new Allergen(allergen));
    }
  }

  Allergens() {
    // Nothing to do here
  }

  void add(Allergen allergen) {
    list.add(allergen);
  }

  JSONArray toJSONTree() {
    JSONArray jsonArray = new JSONArray();
    for (Allergen allergen : list) {
      jsonArray.add(allergen.toJSONTree());
    }
    return jsonArray;
  }

}
