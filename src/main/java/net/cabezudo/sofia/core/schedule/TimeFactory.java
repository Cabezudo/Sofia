package net.cabezudo.sofia.core.schedule;

import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.17
 */
public class TimeFactory {

  private TimeFactory() {
    // Utility classes should not have public constructors
  }

  public static AbstractTime get(int id, TimeType type, int index, int start, int end) throws ClusterException {
    switch (type.getName()) {
      case "time":
        return new Time(id, index, start, end);
      case "day":
        return new DayTime(id, index, start, end);
      case "week":
        return new WeekTime(id, index, start, end);
      case "month":
        return new MonthTime(id, index, start, end);
      case "year":
        return new YearTime(id, index, start, end);
      default:
        throw new SofiaRuntimeException("Invalid time type name: " + type.getName());
    }
  }
}
