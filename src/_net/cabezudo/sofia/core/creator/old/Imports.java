package net.cabezudo.sofia.core.creator;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.28
 */
class Imports implements Iterable<Line> {

  final Set<Line> set;

  Imports() {
    this.set = new TreeSet<>();
  }

  void add(Line line) {
    set.add(line);
  }

  void add(Imports imports) {
    for (Line line : imports) {
      set.add(line);
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Line line : set) {
      sb.append(line);
      sb.append('\n');
    }
    return sb.toString();
  }

  @Override
  public Iterator<Line> iterator() {
    return set.iterator();
  }

  int getSize() {
    return set.size();
  }
}
