package net.cabezudo.sofia.food.helpers;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import net.cabezudo.sofia.core.schedule.AbstractTime;
import net.cabezudo.sofia.core.schedule.Schedule;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.04
 */
public class ScheduleHelper implements Iterable<AbstractTime> {

  private Set<AbstractTime> set;
  private int id;

  ScheduleHelper(Schedule schedule) {
    this();
    this.id = schedule.getId();
    set.addAll(schedule.getTimeList());
  }

  ScheduleHelper() {
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

  void add(AbstractTime time) {
    set.add(time);
  }
}
