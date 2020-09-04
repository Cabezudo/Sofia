package net.cabezudo.sofia.food;

import net.cabezudo.sofia.restaurants.Restaurant;
import net.cabezudo.sofia.restaurants.Schedule;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.02
 */
public class Category {

  private final int id;
  private final Restaurant restaurant;
  private final String name;
  private final Schedule schedule;

  public Category(int id, Restaurant restaurant, String name, Schedule schedule) {
    this.id = id;
    this.restaurant = restaurant;
    this.name = name;
    this.schedule = schedule;
  }

  public int getId() {
    return id;
  }

  public Restaurant getRestaurant() {
    return restaurant;
  }

  public String getName() {
    return name;
  }

  public Schedule getSchedule() {
    return schedule;
  }
}
