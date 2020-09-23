package net.cabezudo.sofia.core.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.22
 */
public class Times {

  private final Map<Integer, List<AbstractTime>> times = new TreeMap<>();

  public void add(int id, AbstractTime time) {
    List<AbstractTime> list = times.get(id);
    if (list == null) {
      list = new ArrayList<>();
      times.put(id, list);
    }
    list.add(time);
  }

  Iterable<AbstractTime> getAll() {
    List<AbstractTime> list = new ArrayList<>();
    for (Map.Entry<Integer, List<AbstractTime>> entry : times.entrySet()) {
      for (AbstractTime time : entry.getValue()) {
        list.add(time);
      }
    }
    return list;
  }

  Iterable<AbstractTime> getByIdl(int id) {
    return times.get(id);
  }
}
