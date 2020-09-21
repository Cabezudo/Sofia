package net.cabezudo.sofia.core.schedule;

import java.time.DayOfWeek;
import java.util.Objects;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.16
 */
public class Day implements Comparable<Day> {

  public static final Day MONDAY = new Day(DayOfWeek.MONDAY.getValue());
  public static final Day TUESDAY = new Day(DayOfWeek.TUESDAY.getValue());
  public static final Day WEDNESDAY = new Day(DayOfWeek.WEDNESDAY.getValue());
  public static final Day THURSDAY = new Day(DayOfWeek.THURSDAY.getValue());
  public static final Day FRIDAY = new Day(DayOfWeek.FRIDAY.getValue());
  public static final Day SATURDAY = new Day(DayOfWeek.SATURDAY.getValue());
  public static final Day SUNDAY = new Day(DayOfWeek.SUNDAY.getValue());

  private final int id;

  private Day(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public static String getShortName(int day) {
    // TODO Ver el tema de internacionalización
    switch (day) {
      case 1:
        return "Lun";
      case 2:
        return "Mar";
      case 3:
        return "Mié";
      case 4:
        return "Jue";
      case 5:
        return "Vie";
      case 6:
        return "Sáb";
      case 7:
        return "Dom";
      default:
        throw new SofiaRuntimeException("Invalid day index: " + day);
    }
  }

  @Override
  public int compareTo(Day d) {
    if (id < d.id) {
      return -1;
    } else {
      return (id == d.id) ? 0 : 1;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Day) {
      Day d = (Day) o;
      return id == d.id;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 53 * hash + Objects.hashCode(this.id);
    return hash;
  }
}
