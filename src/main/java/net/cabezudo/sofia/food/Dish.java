package net.cabezudo.sofia.food;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.core.money.Money;
import net.cabezudo.sofia.food.helpers.DishHelper;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.27
 */
public class Dish {

  private final int id;
  private final String name;
  private final String description;
  private final String imageName;
  private final Allergens allergens;
  private final Integer calories;
  private final Money price;

  public Dish(DishHelper dishHelper) {
    id = dishHelper.getId();
    name = dishHelper.getName();
    description = dishHelper.getDescription();
    imageName = dishHelper.getImageName();
    allergens = new Allergens(dishHelper.getAllergens());
    calories = dishHelper.getCalories();
    price = dishHelper.getPrice();
  }

  Dish(int id, String name, String description, String imageName, Allergens allergens, Integer calories, Money price) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.imageName = imageName;
    this.allergens = allergens;
    this.calories = calories;
    this.price = price;
  }

  public String toJSON() {
    JSONObject jsonRestaurant = new JSONObject();
    jsonRestaurant.add(new JSONPair("id", getId()));
    return jsonRestaurant.toString();
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getImageName() {
    return imageName;
  }

  public Allergens getAllergens() {
    return allergens;
  }

  public Integer getCalories() {
    return calories;
  }

  JSONValue toJSONTree() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.add(new JSONPair("id", id));
    jsonObject.add(new JSONPair("name", name));
    jsonObject.add(new JSONPair("description", description));
    jsonObject.add(new JSONPair("imageName", imageName));
    jsonObject.add(new JSONPair("allergens", allergens.toJSONTree()));
    jsonObject.add(new JSONPair("calories", calories));
    jsonObject.add(new JSONPair("price", price.toJSONTree()));
    return jsonObject;
  }
}
