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
public class DishGroupsHelper implements Iterable<DishGroupHelper> {

  private List<DishGroupHelper> list = new ArrayList<>();
  private Map<Integer, DishGroupHelper> map = new TreeMap<>();

  void add(DishGroupHelper dishGroup) {

    if (map.putIfAbsent(dishGroup.getId(), dishGroup) == null) {
      list.add(dishGroup);
    }
  }

  DishGroupHelper get(int id) {
    return map.get(id);
  }

  @Override
  public Iterator<DishGroupHelper> iterator() {
    return list.iterator();
  }

}
