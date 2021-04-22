package net.cabezudo.sofia.geometry;

class Plane {

  static Point getIntersection(Segment a, Segment b) {
    double x1 = a.a.x;
    double y1 = a.a.y;
    double x2 = a.b.x;
    double y2 = a.b.y;
    double x3 = b.a.x;
    double y3 = b.a.y;
    double x4 = b.b.x;
    double y4 = b.b.y;
    double t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
    double u = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
    if (0 <= t && t <= 1 && 0 <= u && u <= 1) {
      return new Point(x1 + t * (x2 - x1), y1 + t * (y2 - y1));
    }
    return null;
  }

  static double getDistance(Point a, Point b) {
    return Math.hypot(a.x - b.x, a.y - b.y);
  }

  static double getDistance(Point p, Segment s) {
    double x1 = s.a.x;
    double y1 = s.a.y;
    double x2 = s.b.x;
    double y2 = s.b.y;
    double x = p.x;
    double y = p.y;

    double a = x - x1;
    double b = y - y1;
    double c = x2 - x1;
    double d = y2 - y1;

    double dot = a * c + b * d;
    double lineLength = c * c + d * d;
    double param = Double.MAX_VALUE;
    if (lineLength != 0) {
      param = dot / lineLength;
    }

    double xx;
    double yy;

    if (param < 0) {
      xx = x1;
      yy = y1;
    } else if (param > 1) {
      xx = x2;
      yy = y2;
    } else {
      xx = x1 + param * c;
      yy = y1 + param * d;
    }

    double dx = x - xx;
    double dy = y - yy;
    return Math.sqrt(dx * dx + dy * dy);
  }
}
