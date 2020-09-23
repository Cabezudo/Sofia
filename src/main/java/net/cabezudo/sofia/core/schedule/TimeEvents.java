package net.cabezudo.sofia.core.schedule;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import net.cabezudo.sofia.core.Utils;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.18
 */
public class TimeEvents implements Iterable<Event> {

  private Set<Event> set = new TreeSet<>();

  public void add(Event event) {
    set.add(event);
  }

  @Override
  public Iterator<Event> iterator() {
    return set.iterator();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("[ ");
    for (Event event : set) {
      sb.append(event.toString()).append(", ");
    }
    Utils.chop(sb, 2);
    sb.append(" ]");
    return sb.toString();
  }

}
