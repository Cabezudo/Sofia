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
public class OpenTimes implements Iterable<OpenTime> {

  private final List<OpenTime> list = new ArrayList<>();

  public void add(int day, Event start, Event end) {
    OpenTime openTime = new OpenTime(day, start, end);
    list.add(openTime);
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
