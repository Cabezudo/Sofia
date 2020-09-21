package net.cabezudo.sofia.food.helpers;

import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.schedule.AbstractTime;
import net.cabezudo.sofia.core.schedule.Schedule;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.04
 */
public class CategoryHelper {

  private final int id;
  private final String name;
  private final DishGroupsHelper dishGroups = new DishGroupsHelper();
  private final ScheduleHelper schedule;

  CategoryHelper(int id, String name, ScheduleHelper schedule) {
    this.id = id;
    this.name = name;
    this.schedule = schedule;
    if (schedule == null) {
      throw new SofiaRuntimeException("null parameter schedule");
    }
  }

  CategoryHelper(int id, String name, Schedule schedule) {
    this.id = id;
    this.name = name;
    this.schedule = new ScheduleHelper(schedule);
  }

  @Override
  public String toString() {
    return id + ":" + name;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public DishGroupsHelper getDishGroups() {
    return dishGroups;
  }

  public ScheduleHelper getSchedule() {
    return schedule;
  }

  void add(DishGroupHelper dishGroup) {
    dishGroups.add(dishGroup);
  }

  public DishGroupHelper getDishGroup(int id) {
    return dishGroups.get(id);
  }

  void add(AbstractTime time) {
    schedule.add(time);
  }
}
