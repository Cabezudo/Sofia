package net.cabezudo.sofia.core.sites;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.07
 */
public class Sites implements Iterable<Site> {

  private final Map<Integer, Site> map = new TreeMap<>();
  private final List<Site> list = new ArrayList<>();

  public int size() {
    return list.size();
  }

  public Site getById(int id) {
    return map.get(id);
  }

  @Override
  public Iterator<Site> iterator() {
    return list.iterator();
  }

  public void add(Site site) {
    map.put(site.getId(), site);
    list.add(site);
  }

}
