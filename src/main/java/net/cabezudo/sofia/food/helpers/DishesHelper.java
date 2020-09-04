package net.cabezudo.sofia.food.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.04
 */
public class DishesHelper implements Iterable<DishHelper> {

  private List<DishHelper> list = new ArrayList<>();
  private Map<Integer, DishHelper> map = new TreeMap<>();

  void add(DishHelper dish) {
    if (map.putIfAbsent(dish.getId(), dish) == null) {
      list.add(dish);
    }
  }

  @Override
  public Iterator<DishHelper> iterator() {
    return list.iterator();
  }
}
