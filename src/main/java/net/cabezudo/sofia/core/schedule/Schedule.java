package net.cabezudo.sofia.core.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.core.cluster.ClusterException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.27
 */
public class Schedule {

  private final Set<AbstractTime> set = new TreeSet<>();

  public Schedule(JSONArray jsonSchedule) throws ClusterException, JSONParseException {
    for (JSONValue jsonValue : jsonSchedule) {
      AbstractTime abstractTime = TimeFactory.get(jsonValue.toJSONObject());
      set.add(abstractTime);
    }
  }

  public String toJSON() {
    return toJSONTree().toString();
  }

  public JSONArray toJSONTree() {
    JSONArray jsonTimes = new JSONArray();
    set.forEach(time -> {
      jsonTimes.add(time.toJSONTree());
    });
    return jsonTimes;
  }

  public void add(AbstractTime time) {
    set.add(time);
  }

  public List<AbstractTime> getTimeList() {
    return new ArrayList<>(set);
  }
}
