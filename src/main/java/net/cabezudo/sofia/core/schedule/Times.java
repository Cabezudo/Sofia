package net.cabezudo.sofia.core.schedule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.cabezudo.json.values.JSONArray;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.22
 */
public class Times implements Iterable<AbstractTime> {

  private final List<AbstractTime> list = new ArrayList<>();

  public void add(AbstractTime time) {
    list.add(time);
  }

  public int size() {
    return list.size();
  }

  public boolean isNotEmpty() {
    return !list.isEmpty();
  }

  public JSONArray toJSONTree() {
    JSONArray jsonTimes = new JSONArray();
    for (AbstractTime time : list) {
      jsonTimes.add(time.toJSONTree());
    }
    return jsonTimes;
  }

  @Override
  public Iterator<AbstractTime> iterator() {
    return list.iterator();
  }
}
