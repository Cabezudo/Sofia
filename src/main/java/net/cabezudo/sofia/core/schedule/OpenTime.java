package net.cabezudo.sofia.core.schedule;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.20
 */
public class OpenTime {

  private final int day;
  private final Event from;
  private final Event to;

  public OpenTime(int day, Event from, Event to) {
    this.day = day;
    this.from = from;
    this.to = to;
  }

  @Override
  public String toString() {
    return "[ " + day + ", " + from.getHour().toHHmm() + ", " + to.getHour().toHHmm() + " ]";
  }

  public int getDay() {
    return day;
  }

  public Event getStart() {
    return from;
  }

  public boolean isOpen(int time) {
    return from.getHour().getTime() <= time && time < to.getHour().getTime();
  }

  public JSONObject toJSONTree() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.add(new JSONPair("from", from.getHour().toHHmm()));
    jsonObject.add(new JSONPair("to", to.getHour().toHHmm()));
    return jsonObject;
  }
}
