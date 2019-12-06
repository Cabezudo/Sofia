package net.cabezudo.sofia.core.creator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
class CascadingStyleSheetImports implements Iterable<Line> {

  private final Set<Line> set;
  private final List<Line> list;

  CascadingStyleSheetImports() {
    this.set = new TreeSet<>();
    this.list = new ArrayList<>();
  }

  void add(Line line) {
    if (set.add(line)) {
      list.add(line);
    };
  }

  @Override
  public Iterator<Line> iterator() {
    return set.iterator();
  }

}
