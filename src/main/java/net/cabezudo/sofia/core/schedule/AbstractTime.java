package net.cabezudo.sofia.core.schedule;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.04
 */
public abstract class AbstractTime implements Comparable<AbstractTime> {

  private final TimeType type;
  private final int index;
  private final Hour from;
  private final Hour to;

  public AbstractTime(TimeType type, int index, Hour from, Hour to) {
    this.type = type;
    this.index = index;
    this.from = from;
    this.to = to;
  }

  public TimeType getType() {
    return type;
  }

  public int getIndex() {
    return index;
  }

  public Hour getStart() {
    return from;
  }

  public Hour getEnd() {
    return to;
  }

  @Override
  public String toString() {
    return "[ " + getType() + ", " + getIndex() + ", " + getStart() + ", " + getEnd() + " ]";
  }

  @Override
  public int compareTo(AbstractTime time) {
    int c = Integer.compare(getIndex(), time.getIndex());
    if (c != 0) {
      return c;
    }
    int s = Integer.compare(getStart().getTime(), time.getStart().getTime());
    if (s != 0) {
      return s;
    }
    return Integer.compare(getEnd().getTime(), time.getEnd().getTime());
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof AbstractTime)) {
      return false;
    }
    AbstractTime t = (AbstractTime) o;
    if (getIndex() != t.getIndex()) {
      return false;
    }
    if (getStart() != t.getStart()) {
      return false;
    }
    return getEnd() != t.getEnd();
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 17 * hash + this.getIndex();
    hash = 17 * hash + this.getStart().getTime();
    hash = 17 * hash + this.getEnd().getTime();
    return hash;
  }

  public JSONValue toJSONTree() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.add(new JSONPair(getType().toString().toLowerCase(), getType().getName(getIndex())));
    jsonObject.add(new JSONPair("from", getStart().toHHmm()));
    jsonObject.add(new JSONPair("to", getEnd().toHHmm()));
    return jsonObject;
  }

  public abstract boolean dayIs(int day);
}
