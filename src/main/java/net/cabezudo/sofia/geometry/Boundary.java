package net.cabezudo.sofia.geometry;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.19
 */
class Boundary extends Polygon {

  final Segment upper;
  final Segment right;
  final Segment lower;
  final Segment left;

  Boundary(Point a, Point b, Point c, Point d) {
    this.upper = new Segment(a, b);
    this.right = new Segment(b, c);
    this.lower = new Segment(c, d);
    this.left = new Segment(d, a);

  }

  Segment getSegmentFor(Point point) {
    if (point.y == upper.a.y) {
      return upper;
    }
    if (point.x == right.a.x) {
      return right;
    }
    if (point.y == lower.a.y) {
      return lower;
    }
    if (point.x == left.a.x) {
      return left;
    }
    return null;
  }

  @Override
  public String toString() {
    return upper + " - " + right + " - " + lower + " - " + left;
  }

}
