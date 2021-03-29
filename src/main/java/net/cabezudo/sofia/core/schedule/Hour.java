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

  public Hour(String time) {
    // TODO check time and values
    String[] parts = time.split(":");
    this.hour = Integer.parseInt(parts[0]);
    this.minutes = Integer.parseInt(parts[1]);
    if (parts.length > 2) {
      this.seconds = Integer.parseInt(parts[2]);
    } else {
      this.seconds = 0;
    }
    this.time = ((hour * 60) + minutes) * 60 + seconds;
  }

  public Hour(int time) {
    this.time = time;
    int secondsRemaind = time / 60;
    seconds = time % 60;
    int minutesRemaind = secondsRemaind / 60;
    minutes = minutesRemaind % 60;
    int houresRemaind = minutesRemaind / 60;
    hour = houresRemaind % 60;
  }

  public Hour(int hour, int minutes, int seconds) {
    this.hour = hour;
    this.minutes = minutes;
    this.seconds = seconds;
    this.time = ((hour * 60) + minutes) * 60 + seconds;
  }

  public int getTime() {
    return time;
  }

  public String toHHmm() {
    return (hour > 9 ? hour : "0" + hour) + ":" + (minutes > 9 ? minutes : "0" + minutes);
  }

  public String toHHmmss() {
    return (hour > 9 ? hour : "0" + hour) + ":" + (minutes > 9 ? minutes : "0" + minutes) + ":" + (seconds > 9 ? seconds : "0" + seconds);
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
