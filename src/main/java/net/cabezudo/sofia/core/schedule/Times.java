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

  private final Map<Integer, List<AbstractTime>> map = new TreeMap<>();

  public void add(int id, AbstractTime time) {
    List<AbstractTime> list = map.get(id);
    if (list == null) {
      list = new ArrayList<>();
      map.put(id, list);
    }
    list.add(time);
  }

  public Iterable<AbstractTime> getAll() {
    List<AbstractTime> list = new ArrayList<>();
    map.entrySet().forEach(entry -> entry.getValue().forEach(time -> list.add(time)));
    return list;
  }

  public Iterable<AbstractTime> getByIdl(int id) {
    return map.get(id);
  }
}
