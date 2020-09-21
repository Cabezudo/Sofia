package net.cabezudo.sofia.core.schedule;

import net.cabezudo.sofia.core.Utils;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.19
 */
public class Event implements Comparable<Event> {

  protected static final int START = 1;
  protected static final int END = 2;

  private final int type;
  private final int day;
  private final int time;

  Event(int type, int day, int time) {
    this.type = type;
    this.day = day;
    this.time = time;
  }

  public int getDay() {
    return day;
  }

  public int getTime() {
    return time;
  }

  @Override
  public String toString() {
    return "[ " + day + ", " + (isStart() ? "S" : "E") + ", " + Utils.toHour(time) + " ]";
  }

  @Override
  public int compareTo(Event e) {
    int d = Integer.compare(day, e.day);
    if (d != 0) {
      return d;
    }
    return Integer.compare(time, e.time);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Event) {
      Event e = (Event) o;
      return day == e.day && time == e.time;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 53 * hash + this.day;
    hash = 53 * hash + this.time;
    return hash;
  }

  public boolean isStart() {
    return type == START;
  }

  public boolean isEnd() {
    return type == END;
  }
}
