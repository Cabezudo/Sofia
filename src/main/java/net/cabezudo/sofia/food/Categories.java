package net.cabezudo.sofia.food;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.schedule.Schedule;
import net.cabezudo.sofia.food.helpers.CategoriesHelper;
import net.cabezudo.sofia.food.helpers.CategoryHelper;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.03
 */
public class Categories implements Iterable<Category> {

  private final Map<Integer, Category> map = new TreeMap<>();

  public Categories(CategoriesHelper categoriesHelper) {
    for (CategoryHelper categoryHelper : categoriesHelper) {
      Category category = new Category(categoryHelper);
      add(category);
    }
  }

  public String toJSON() {
    return toJSONTree().toString();
  }

  public final void add(Category c) {
    Category category = map.get(c.getId());
    if (category == null) {
      map.put(c.getId(), c);
    }
  }

  public Category get(Category c) {
    return map.get(c.getId());
  }

  public JSONArray toJSONTree() {
    JSONArray jsonCategories = new JSONArray();
    map.entrySet().stream().map(entry -> entry.getValue()).forEachOrdered(category -> jsonCategories.add(category.toJSONTree()));
    return jsonCategories;
  }

  Schedule getSchedule(int categoryId) {
    Category category = map.get(categoryId);
    if (category == null) {
      throw new SofiaRuntimeException("Category NOT FOUND for id " + categoryId);
    }
    return category.getSchedule();
  }

  @Override
  public Iterator<Category> iterator() {
    return map.values().iterator();
  }
}
