package net.cabezudo.sofia.core.schedule;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.schedule.Event;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.20
 */
public class OpenTime {

  private final int day;
  private final Event start;
  private final Event end;

  public OpenTime(int day, Event start, Event end) {
    this.day = day;
    this.start = start;
    this.end = end;
  }

  @Override
  public String toString() {
    return "[ " + day + ", " + Utils.toHour(start.getTime()) + ", " + Utils.toHour(end.getTime()) + " ]";
  }

  public int getDay() {
    return day;
  }

  public Event getStart() {
    return start;
  }

  public boolean isOpen(int time) {
    return start.getTime() <= time && time < end.getTime();
  }

  public JSONObject toJSONTree() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.add(new JSONPair("start", Utils.toHour(start.getTime())));
    jsonObject.add(new JSONPair("end", Utils.toHour(end.getTime())));
    return jsonObject;
  }
}
