package net.cabezudo.sofia.core.schedule;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.04
 */
public class ScheduleHelper implements Iterable<AbstractTime> {

  private Set<AbstractTime> set;
  private int id;

  public ScheduleHelper(Schedule schedule) {
    this();
    this.id = schedule.getId();
    set.addAll(schedule.getTimeList());
  }

  public ScheduleHelper() {
    this.set = new TreeSet<>();
  }

  public int getId() {
    return id;
  }

  @Override
  public Iterator<AbstractTime> iterator() {
    return set.iterator();
  }

  public boolean isEmpty() {
    return set.isEmpty();
  }

  public void add(AbstractTime time) {
    set.add(time);
  }
}
