package net.cabezudo.sofia.food.helpers;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.04
 */
public class ScheduleHelper implements Iterable<TimeHelper> {

  private Set<TimeHelper> set = new TreeSet<TimeHelper>();

  @Override
  public Iterator<TimeHelper> iterator() {
    return set.iterator();
  }

  public boolean isEmpty() {
    return set.isEmpty();
  }

}
