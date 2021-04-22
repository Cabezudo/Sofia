package net.cabezudo.sofia.geometry;

class Intersection {

  private final Segment a;
  private final Segment b;
  private final Point p;

  @Override
  public String toString() {
    return a + " " + b + " " + p;
  }

  Intersection(Segment a, Segment b, Point p) {
    this.a = a;
    this.b = b;
    this.p = p;
  }

  Point getPoint() {
    return p;
  }

  Segment getSegmentA() {
    return a;
  }

  Segment getSegmentB() {
    return b;
  }

}
