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
public class Allergens {

  private List<Allergen> list = new ArrayList<>();

  Allergens(AllergensHelper allergens) {
    for (AllergenHelper allergen : allergens) {
      add(new Allergen(allergen));
    }
  }

  Allergens() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  void add(Allergen allergen) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  JSONArray toJSONTree() {
    JSONArray jsonArray = new JSONArray();
    for (Allergen allergen : list) {
      jsonArray.add(allergen.toJSONTree());
    }
    return jsonArray;
  }

}
