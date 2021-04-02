package net.cabezudo.sofia.core.schedule;

import java.time.DayOfWeek;
import java.util.Objects;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.languages.Language;

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

  private final int index;

  public Day(int index) {
    this.index = index;
  }

  public int getId() {
    return index;
  }

  public String getShortName(Language language) {
    // TODO Read the language names on demand from a file and store in a cache
    switch (language.getTwoLetterCode()) {
      case "es":
        switch (index) {
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
            throw new SofiaRuntimeException("Invalid day index: " + index);
        }
      default:
        switch (index) {
          case 1:
            return "Mon";
          case 2:
            return "Tue";
          case 3:
            return "Wen";
          case 4:
            return "Thu";
          case 5:
            return "Fri";
          case 6:
            return "Sat";
          case 7:
            return "Sun";
          default:
            throw new SofiaRuntimeException("Invalid day index: " + index);
        }
    }
  }

  public String getName(Language language) {
    // TODO Read the language names on demand from a file and store in a cache
    switch (language.getTwoLetterCode()) {
      case "es":
        switch (index) {
          case 1:
            return "lunes";
          case 2:
            return "martes";
          case 3:
            return "miércoles";
          case 4:
            return "jueves";
          case 5:
            return "viernes";
          case 6:
            return "sábado";
          case 7:
            return "domingo";
          default:
            throw new SofiaRuntimeException("Invalid day index: " + index);
        }
      default:
        switch (index) {
          case 1:
            return "Monday";
          case 2:
            return "Tuesday";
          case 3:
            return "Wednesday";
          case 4:
            return "Thursday";
          case 5:
            return "Friday";
          case 6:
            return "Saturday";
          case 7:
            return "Sunday";
          default:
            throw new SofiaRuntimeException("Invalid day index: " + index);
        }
    }
  }

  @Override
  public int compareTo(Day d) {
    if (index < d.index) {
      return -1;
    } else {
      return (index == d.index) ? 0 : 1;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Day) {
      Day d = (Day) o;
      return index == d.index;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 53 * hash + Objects.hashCode(this.index);
    return hash;
  }
}
