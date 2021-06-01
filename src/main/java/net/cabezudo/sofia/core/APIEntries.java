package net.cabezudo.sofia.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.10.19
 */
public class APIEntries implements Iterable<APIEntry> {

  private final List<APIEntry> list = new ArrayList<>();

  public void add(APIEntry apiEntry) {
    list.add(apiEntry);
  }

  @Override
  public Iterator<APIEntry> iterator() {
    return list.iterator();
  }
}
