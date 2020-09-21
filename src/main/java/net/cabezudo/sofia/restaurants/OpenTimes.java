package net.cabezudo.sofia.restaurants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.sofia.core.schedule.Event;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.20
 */
class OpenTimes implements Iterable<OpenTime> {

  private final List<OpenTime> list = new ArrayList<>();

  void add(int day, Event start, Event end) {
    list.add(new OpenTime(day, start, end));
  }

  @Override
  public Iterator<OpenTime> iterator() {
    return list.iterator();
  }

  public JSONArray toJSONTree() {
    JSONArray jsonArray = new JSONArray();
    for (OpenTime time : list) {
      jsonArray.add(time.toJSONTree());
    }
    return jsonArray;
  }

}
