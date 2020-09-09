package net.cabezudo.sofia.food;

import java.util.Map;
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
    return toJSONTree().toString();
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

  JSONArray toJSONTree() {
    JSONArray jsonCategories = new JSONArray();
    map.entrySet().stream().map(entry -> entry.getValue()).forEachOrdered(category -> jsonCategories.add(category.toJSONTree()));
    return jsonCategories;
  }

}
