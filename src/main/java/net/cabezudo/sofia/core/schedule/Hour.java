package net.cabezudo.sofia.core.schedule;

import java.util.Objects;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.16
 */
public class Hour implements Comparable<Hour> {

  private final int time;
  private final int hour;
  private final int minutes;
  private final int seconds;

  public Hour(int hour, int minutes, int seconds) {
    this.hour = hour;
    this.minutes = minutes;
    this.seconds = seconds;
    this.time = ((hour * 60) + minutes) * 60 + seconds;
  }

  public int getTime() {
    return time;
  }

  @Override
  public int compareTo(Hour h) {
    if (time < h.time) {
      return -1;
    } else {
      return (time == h.time) ? 0 : 1;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Hour) {
      Hour h = (Hour) o;
      return time == h.time;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 53 * hash + Objects.hashCode(this.time);
    return hash;
  }
}
