package net.cabezudo.sofia.restaurants;

import java.util.Set;
import java.util.TreeSet;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.food.Time;
import net.cabezudo.sofia.food.helpers.ScheduleHelper;
import net.cabezudo.sofia.food.helpers.TimeHelper;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.27
 */
public class Schedule {

  private final Set<Time> set = new TreeSet<>();

  public Schedule(ScheduleHelper schedule) {
    if (!schedule.isEmpty()) {
      for (TimeHelper time : schedule) {
        set.add(new Time(time));
      }
    }
  }

  public Schedule() {
    // Nothing to do here
  }

  JSONObject toJSON() {
    return new JSONObject();
  }

  public void add(Time time) {
    set.add(time);
  }

  public JSONArray toJSONTree() {
    // TODO Calculate the adjacent times as one.
    return new JSONArray();
  }

}
