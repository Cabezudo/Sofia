package net.cabezudo.sofia.restaurants;

import net.cabezudo.sofia.food.Categories;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.18
 */
public class RestaurantSchedule {

  private final RestaurantBusinessHours restaurantBusinessHours;

  public RestaurantSchedule(Categories categories, int offset) {
    this.restaurantBusinessHours = new RestaurantBusinessHours(categories, offset);
  }

  public RestaurantBusinessHours getBusinessHours() {
    return restaurantBusinessHours;
  }
}
