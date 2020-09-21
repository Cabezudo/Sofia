package net.cabezudo.sofia.food.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.cabezudo.sofia.core.schedule.AbstractTime;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.04
 */
public class CategoriesHelper implements Iterable<CategoryHelper> {

  private Map<Integer, CategoryHelper> map = new TreeMap<>();
  private List<CategoryHelper> list = new ArrayList<>();

  void add(CategoryHelper category) {
    if (map.putIfAbsent(category.getId(), category) == null) {
      list.add(category);
    }
  }

  CategoryHelper get(int id) {
    return map.get(id);
  }

  @Override
  public Iterator<CategoryHelper> iterator() {
    return list.iterator();
  }

  public void add(Integer categoryId, String categoryName, AbstractTime time) {
    CategoryHelper category = map.get(categoryId);
    if (category == null) {
      ScheduleHelper schedule = new ScheduleHelper();
      category = new CategoryHelper(categoryId, categoryName, schedule);
      add(category);
    }
    category.add(time);
  }

}
