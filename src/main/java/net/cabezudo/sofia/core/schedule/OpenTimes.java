package net.cabezudo.sofia.core.schedule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.cabezudo.json.values.JSONArray;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.20
 */
public class OpenTimes implements Iterable<OpenTime> {

  private final List<OpenTime> list = new ArrayList<>();

  public void add(int day, Event open, Event close) {
    OpenTime openTime = new OpenTime(day, open, close);
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

  public int size() {
    return list.size();
  }

  public OpenTime get(int i) {
    return list.get(i);
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }

}
