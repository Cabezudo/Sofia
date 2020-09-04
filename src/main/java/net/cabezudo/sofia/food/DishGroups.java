package net.cabezudo.sofia.food;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.sofia.food.helpers.DishGroupHelper;
import net.cabezudo.sofia.food.helpers.DishGroupsHelper;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.04
 */
public class DishGroups implements Iterable<DishGroup> {

  private List<DishGroup> list = new ArrayList<>();

  DishGroups(DishGroupsHelper dishGroups) {
    for (DishGroupHelper dishGroup : dishGroups) {
      add(new DishGroup(dishGroup));
    }
  }

  DishGroups() {
    // Nothing to do here
  }

  private void add(DishGroup dishGroup) {
    list.add(dishGroup);
  }

  @Override
  public Iterator<DishGroup> iterator() {
    return list.iterator();
  }

  JSONArray toJSONTree() {
    JSONArray jsonArray = new JSONArray();
    for (DishGroup dishGroup : list) {
      jsonArray.add(dishGroup.toJSONTree());
    }
    return jsonArray;
  }

}
