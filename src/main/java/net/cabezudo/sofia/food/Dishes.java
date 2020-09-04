package net.cabezudo.sofia.food;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.sofia.food.helpers.DishHelper;
import net.cabezudo.sofia.food.helpers.DishesHelper;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.29
 */
public class Dishes implements Iterable<Dish> {

  private final Map<Integer, Dish> map = new TreeMap<>();
  private final List<Dish> list = new ArrayList<>();

  Dishes(DishesHelper dishes) {
    for (DishHelper dish : dishes) {
      add(new Dish(dish));
    }
  }

  Dishes() {

  }

  public int size() {
    return list.size();
  }

  public Dish getById(int id) {
    return map.get(id);
  }

  @Override
  public Iterator<Dish> iterator() {
    return list.iterator();
  }

  public void add(Dish dish) {
    map.put(dish.getId(), dish);
    list.add(dish);
  }

  JSONArray toJSONTree() {
    JSONArray jsonArray = new JSONArray();
    for (Dish dish : list) {
      jsonArray.add(dish.toJSONTree());
    }
    return jsonArray;
  }

}
