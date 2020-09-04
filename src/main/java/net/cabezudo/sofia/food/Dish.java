package net.cabezudo.sofia.food;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.27
 */
public class Dish {

  private final int id;
  private final DishGroup dishGroup;
  private final String name;
  private final String description;
  private final String image;
  private final Allergens allergens;
  private final Integer calories;

  public Dish(DishHelper dishHelper) {
    id = dishHelper.getId();
    dishGroup = dishHelper.getDishGroup();
    name = dishHelper.getName();
    description = dishHelper.getDescription();
    image = dishHelper.getImage();
    allergens = dishHelper.getAllergens();
    calories = dishHelper.getCalories();
  }

  Dish(int id, DishGroup dishGroup, String name, String description, String image, Integer calories) {
    this.id = id;
    this.dishGroup = dishGroup;
    this.name = name;
    this.description = description;
    this.image = image;
    this.allergens = new Allergens();
    this.calories = calories;
  }

  public String toJSON() {
    JSONObject jsonRestaurant = new JSONObject();
    jsonRestaurant.add(new JSONPair("id", getId()));
    return jsonRestaurant.toString();
  }

  public int getId() {
    return id;
  }

  public DishGroup getCategory() {
    return dishGroup;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getImage() {
    return image;
  }

  public Allergens getAllergens() {
    return allergens;
  }

  public Integer getCalories() {
    return calories;
  }
}
