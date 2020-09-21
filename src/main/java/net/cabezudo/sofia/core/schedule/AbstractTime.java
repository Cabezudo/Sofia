package net.cabezudo.sofia.core.schedule;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.04
 */
public abstract class AbstractTime implements Comparable<AbstractTime> {

  private final int id;
  private final TimeType type;
  private final int index;
  private final int start;
  private final int end;

  public AbstractTime(int id, TimeType type, int index, int start, int end) {
    this.id = id;
    this.type = type;
    this.index = index;
    this.start = start;
    this.end = end;
  }

  public int getId() {
    return id;
  }

  public TimeType getType() {
    return type;
  }

  public int getIndex() {
    return index;
  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return end;
  }

  @Override
  public String toString() {
    return "[ " + getId() + ", " + getType() + ", " + getIndex() + ", " + getStart() + ", " + getEnd() + " ]";
  }

  @Override
  public int compareTo(AbstractTime time) {
    int c = Integer.compare(getIndex(), time.getIndex());
    if (c != 0) {
      return c;
    }
    int s = Integer.compare(getStart(), time.getStart());
    if (s != 0) {
      return s;
    }
    return Integer.compare(getEnd(), time.getEnd());
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
    hash = 17 * hash + this.getStart();
    hash = 17 * hash + this.getEnd();
    return hash;
  }

  public JSONValue toJSONTree() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.add(new JSONPair("id", getId()));
    jsonObject.add(new JSONPair("type", getType().toJSONTree()));
    jsonObject.add(new JSONPair("index", getIndex()));
    jsonObject.add(new JSONPair("start", getStart()));
    jsonObject.add(new JSONPair("end", getEnd()));
    return jsonObject;
  }

  public abstract boolean dayIs(int day);
}
