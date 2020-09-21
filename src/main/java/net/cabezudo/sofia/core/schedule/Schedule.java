package net.cabezudo.sofia.core.schedule;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.food.helpers.ScheduleHelper;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.27
 */
public class Schedule {

  private final int id;
  private final Set<AbstractTime> set = new TreeSet<>();

  public Schedule(ScheduleHelper schedule) {
    this.id = schedule.getId();

    if (!schedule.isEmpty()) {
      for (AbstractTime time : schedule) {
        set.add(time);
      }
    }
  }

  public Schedule(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public String toJSON() {
    return toJSONTree().toString();
  }

  public JSONObject toJSONTree() {
    JSONObject jsonObject = new JSONObject();

    jsonObject.add(new JSONPair("id", id));

    JSONArray jsonTimes = new JSONArray();
    for (AbstractTime time : set) {
      jsonTimes.add(time.toJSONTree());
    }
    jsonObject.add(new JSONPair("times", jsonTimes));
    return jsonObject;
  }

  public void add(AbstractTime time) {
    set.add(time);
  }

  public ArrayList<AbstractTime> getTimeList() {
    return new ArrayList<>(set);
  }
}
