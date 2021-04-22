package net.cabezudo.sofia.geometry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Points implements Iterable<Point> {

  private final List<Point> list = new ArrayList<>();
  private final Set<Point> set = new TreeSet<>();

  void add(Point point) {
    set.add(point);
    list.add(point);

  }

  @Override
  public Iterator<Point> iterator() {
    return list.iterator();
  }

  public int size() {
    return list.size();
  }

  Point get(int i) {
    return list.get(i);
  }

  boolean contains(Point p) {
    return set.contains(p);
  }

}
