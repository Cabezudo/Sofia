package net.cabezudo.sofia.core.schedule;

import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONObject;
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

  public static AbstractTime get(JSONObject jsonTime) throws ClusterException, JSONParseException {
    String timeType = jsonTime.getNullString("time");
    if (timeType != null) {
      // TODO Convert the hours in minutes in the day for comparation
      Hour from = null;
      Hour to = null;
      return new Time(0, from, to);
    }
    String dayType = jsonTime.getNullString("day");
    if (dayType != null) {
      int index = getDayIndex(jsonTime);
      Hour from;
      Hour to;
      try {
        from = new Hour(jsonTime.getString("from"));
      } catch (PropertyNotExistException e) {
        throw new JSONParseException("The 'from' property not found", jsonTime.getPosition());
      }
      try {
        to = new Hour(jsonTime.getString("to"));
      } catch (PropertyNotExistException e) {
        throw new JSONParseException("The 'to' property not found", jsonTime.getPosition());
      }
      return new DayTime(index, from, to);
    }
    throw new UnsupportedOperationException("Not supported yet or invalid type.");
  }

  private static int getDayIndex(JSONObject jsonTime) throws JSONParseException {
    String dayName = jsonTime.getNullString("day");
    switch (dayName) {
      case "MONDAY":
        return 1;
      case "TUESDAY":
        return 2;
      case "WEDNESDAY":
        return 3;
      case "THURSDAY":
        return 4;
      case "FRIDAY":
        return 5;
      case "SATURDAY":
        return 6;
      case "SUNDAY":
        return 7;
      default:
        throw new JSONParseException("Invalid day name: " + dayName, jsonTime.getPosition());
    }
  }

  public static AbstractTime get(TimeType type, int index, Hour from, Hour to) throws ClusterException {
    switch (type.name()) {
      case "time":
        return new Time(index, from, to);
      case "day":
        return new DayTime(index, from, to);
      case "week":
        return new WeekTime(index, from, to);
      case "month":
        return new MonthTime(index, from, to);
      case "year":
        return new YearTime(index, from, to);
      default:
        throw new SofiaRuntimeException("Invalid time type name: " + type);
    }
  }
}
