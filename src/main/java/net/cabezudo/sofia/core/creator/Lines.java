package net.cabezudo.sofia.core.creator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
abstract class Lines implements Iterable<Line> {

  List<Line> lines = new ArrayList<>();

  protected abstract boolean filter(Line line);

  void add(Line line) {
    if (filter(line)) {
      return;
    }
    lines.add(line);
  }

  void add(Lines ls) {
    if (ls == null) {
      return;
    }
    for (Line line : ls) {
      add(line);
    }
  }

  @Override
  public Iterator<Line> iterator() {
    return lines.iterator();
  }

  int getSize() {
    return lines.size();
  }

  Line get(int i
  ) {
    return lines.get(i);
  }

  void set(int i, Line line) {
    lines.set(i, line);
  }

  boolean isNotEmpty() {
    return !lines.isEmpty();
  }

  String getCode() {
    StringBuilder sb = new StringBuilder();
    for (Line line : lines) {
      sb.append(line.getCode());
      sb.append('\n');
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return getCode();
  }
}
