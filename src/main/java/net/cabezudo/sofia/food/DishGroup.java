package net.cabezudo.sofia.food;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.food.helpers.DishGroupHelper;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.02
 */
public class DishGroup {

  private final int id;
  private final String name;
  private final Dishes dishes;

  public DishGroup(int id, String name, Dishes dishes) {
    this.id = id;
    this.name = name;
    this.dishes = dishes;
  }

  DishGroup(DishGroupHelper dishGroup) {
    this.id = dishGroup.getId();
    this.name = dishGroup.getName();
    this.dishes = new Dishes(dishGroup.getDishes());
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Dishes getDishes() {
    return dishes;
  }

  JSONValue toJSONTree() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.add(new JSONPair("id", id));
    jsonObject.add(new JSONPair("name", name));
    jsonObject.add(new JSONPair("dishes", dishes.toJSONTree()));
    return jsonObject;
  }
}
