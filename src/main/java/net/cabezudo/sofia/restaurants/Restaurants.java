package net.cabezudo.sofia.restaurants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import net.cabezudo.sofia.core.schedule.BusinessHours;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.29
 */
public class Restaurants implements Iterable<Restaurant> {

  Restaurants() {
    //Nothing to do here
  }

  Restaurants(List<Restaurant> list) {
  }

  private final Map<Integer, Restaurant> map = new TreeMap<>();
  private final List<Restaurant> list = new ArrayList<>();
  private final Set<Restaurant> set = new TreeSet<>();

  public int size() {
    return list.size();
  }

  public Restaurant getById(int id) {
    return map.get(id);
  }

  @Override
  public Iterator<Restaurant> iterator() {
    return set.iterator();
  }

  public void add(Restaurant restaurant) {
    map.put(restaurant.getId(), restaurant);
    list.add(restaurant);
    set.add(restaurant);
  }

  Restaurants calculateFor(int timeZoneOffset) {
    for (Restaurant restaurant : list) {
      BusinessHours businessHours = restaurant.getBusinessHours();
      businessHours.calculateFor(timeZoneOffset);
    }
    return new Restaurants(list);
  }
}
