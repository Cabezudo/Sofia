package net.cabezudo.sofia.food;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.sofia.food.helpers.CategoriesHelper;
import net.cabezudo.sofia.food.helpers.CategoryHelper;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.03
 */
class Categories {

  private Map<Integer, Category> map = new TreeMap<>();

  Categories(CategoriesHelper categoriesHelper) {
    for (CategoryHelper categoryHelper : categoriesHelper) {
      Category category = new Category(categoryHelper);
      map.put(category.getId(), category);
    }
  }

  String toJSON() {
    JSONArray jsonCategories = new JSONArray();
    for (Entry<Integer, Category> entry : map.entrySet()) {
      Category category = entry.getValue();
      jsonCategories.add(category.toJSONTree());
    }
    return jsonCategories.toJSON();
  }

  void add(Category c) {
    Category category = map.get(c.getId());
    if (category == null) {
      map.put(c.getId(), c);
    }
  }

  Category get(Category c) {
    return map.get(c.getId());
  }

}
