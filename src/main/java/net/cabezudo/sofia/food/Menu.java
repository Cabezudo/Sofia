package net.cabezudo.sofia.food;

import net.cabezudo.json.values.JSONArray;
import net.cabezudo.sofia.food.helpers.MenuHelper;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.03
 */
public class Menu {

  private final Categories categories;

  Menu(Categories categories) {
    this.categories = categories;
  }

  Menu(MenuHelper menuHelper) {
    this.categories = new Categories(menuHelper.getCategories());
  }

  public String toJSON() {
    return categories.toJSON();
  }

  public JSONArray toJSONTree() {
    return categories.toJSONTree();
  }
}
