package net.cabezudo.sofia.core.creator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
abstract class Lines implements Iterable<Line> {

  List<Line> list = new ArrayList<>();

  protected abstract Line transform(Line line);

  void add(Line line) {
    line = transform(line);
    if (line == null) {
      return;
    }
    list.add(line);
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
    return list.iterator();
  }

  int getSize() {
    return list.size();
  }

  Line get(int i
  ) {
    return list.get(i);
  }

  void set(int i, Line line) {
    list.set(i, line);
  }

  boolean isNotEmpty() {
    return !list.isEmpty();
  }

  String getCode() {
    StringBuilder sb = new StringBuilder();
    for (Line line : list) {
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
