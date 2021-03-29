package net.cabezudo.sofia.core.schedule;

import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.29
 */
public enum TimeType {
  TIME, DAY, WEEK, MONTH, YEAR;

  String getName(int index) {
    switch (this.name()) {
      case "DAY":
        switch (index) {
          case 1:
            return "MONDAY";
          case 2:
            return "TUESDAY";
          case 3:
            return "WEDNESDAY";
          case 4:
            return "THURSDAY";
          case 5:
            return "FRIDAY";
          case 6:
            return "SATURDAY";
          case 7:
            return "SUNDAY";
          default:
            throw new SofiaRuntimeException("Invalid index for day");
        }
      default:
        throw new SofiaRuntimeException("Invalid time type");
    }
  }
}
