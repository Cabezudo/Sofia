package net.cabezudo.sofia.core.schedule;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.19
 */
public class Event implements Comparable<Event> {

  protected static final int START = 1;
  protected static final int END = 2;

  private final int type;
  private final int day;
  private final Hour hour;

  Event(int type, int day, Hour hour) {
    this.type = type;
    this.day = day;
    this.hour = hour;
  }

  public int getDay() {
    return day;
  }

  public Hour getHour() {
    return hour;
  }

  @Override
  public String toString() {
    return "[ " + day + ", " + (isStart() ? "S" : "E") + ", " + hour.toHHmm() + " ]";
  }

  @Override
  public int compareTo(Event e) {
    int d = Integer.compare(day, e.day);
    if (d != 0) {
      return d;
    }
    return Integer.compare(hour.getTime(), e.hour.getTime());
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Event) {
      Event e = (Event) o;
      return day == e.day && hour == e.hour;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 53 * hash + this.day;
    hash = 53 * hash + this.hour.getTime();
    return hash;
  }

  public boolean isStart() {
    return type == START;
  }

  public boolean isEnd() {
    return type == END;
  }

  public Object toJSONTree() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
