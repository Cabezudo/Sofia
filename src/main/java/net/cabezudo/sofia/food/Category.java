package net.cabezudo.sofia.food;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.core.schedule.Schedule;
import net.cabezudo.sofia.food.helpers.CategoryHelper;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.02
 */
public class Category {

  private final int id;
  private final String name;
  private final DishGroups dishGroups;
  private final Schedule schedule;

  public Category(int id, String name, DishGroups dishGroups, Schedule schedule) {
    this.id = id;
    this.name = name;
    this.dishGroups = dishGroups;
    this.schedule = schedule;
  }

  public Category(CategoryHelper categoryHelper) {
    this.id = categoryHelper.getId();
    this.name = categoryHelper.getName();
    this.dishGroups = new DishGroups(categoryHelper.getDishGroups());
    this.schedule = new Schedule(categoryHelper.getSchedule());
  }

  @Override
  public String toString() {
    return id + ":" + name;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public DishGroups getDishGroups() {
    return dishGroups;
  }

  public Schedule getSchedule() {
    return schedule;
  }

  JSONValue toJSONTree() {
    JSONObject jsonCategory = new JSONObject();
    jsonCategory.add(new JSONPair("id", id));
    jsonCategory.add(new JSONPair("name", name));
    jsonCategory.add(new JSONPair("dishGroups", dishGroups.toJSONTree()));
    return jsonCategory;
  }
}
