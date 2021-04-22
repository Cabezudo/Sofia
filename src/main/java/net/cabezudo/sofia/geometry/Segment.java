package net.cabezudo.sofia.geometry;

import java.util.Objects;

public class Segment {

  public final Point a;
  public final Point b;

  Segment(Point a, Point b) {
    this.a = a;
    this.b = b;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (o instanceof Segment) {
      Segment s = (Segment) o;
      return a.equals(s.a) && b.equals(s.b);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 59 * hash + Objects.hashCode(this.a);
    hash = 59 * hash + Objects.hashCode(this.b);
    return hash;
  }

  @Override
  public String toString() {
    return "a = " + a + ". b = " + b;
  }

  boolean contains(Point p) {
    double x1 = a.x;
    double y1 = a.y;
    double x2 = b.x;
    double y2 = b.y;
    double px = p.x;
    double py = p.y;
    if (x1 == x2 && x2 == px) {
      return (Math.min(y1, y2) <= py && py <= Math.max(y1, y2));
    }
    double slope = (y2 - y1) / (x2 - x1);
    boolean onLine = (py - y1) == slope * (px - x1);
    boolean onSegment = (Math.min(x1, x2) <= px && px <= Math.max(x1, x2)) && (Math.min(y1, y2) <= py && py <= Math.max(y1, y2));
    return onLine && onSegment;
  }
}
