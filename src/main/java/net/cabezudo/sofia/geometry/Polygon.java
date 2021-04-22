package net.cabezudo.sofia.geometry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;

public class Polygon implements Iterable<Point> {

  private final Points points = new Points();
  private Double minX;
  private Double maxX;
  private Double minY;
  private Double maxY;

  public Polygon() {
    // Nothing to do here
  }

  public Polygon(Polygon a, Polygon b) {
    for (Point p : a) {
      add(p);
    }
    for (Point p : b) {
      add(p);
    }
  }

  public Polygon(Polygon polygon, int start, int end) {
    for (int i = start; i < end; i++) {
      add(polygon.get(i));
    }
  }

  public Polygon(Point... ps) {
    for (Point point : ps) {
      add(point);
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(20 + points.size() * 15);
    sb.append("(").append(minX).append(", ").append(maxX).append(", ").append(minY).append(", ").append(maxY).append(")\n");
    sb.append(points.toString());
    return sb.toString();
  }

  // Ray-casting algorithm
  public boolean isInside(Point p) {
    boolean inside = false;
    for (int i = 0; i < points.size(); i++) {
      Point a = points.get(i);
      Point b = points.get((i + 1) % points.size());
      if (a.equals(p)) {
        return true;
      }
      Segment segment = new Segment(a, b);

      if (intersects(segment, p)) {
        inside = !inside;
      }
    }
    return inside;
  }

  private boolean intersects(Segment s, Point p) {
    Point a, b;

    if (s.a.y > s.b.y) {
      a = s.b;
      b = s.a;
    } else {
      a = s.a;
      b = s.b;
    }

    Double x = p.x;
    Double y = p.y;

    if (y == a.y || y == b.y) {
      y += 0.00001;
    }

    if (y < a.y || y > b.y || x > Math.max(a.x, b.x)) {
      return false;
    }

    if (x < Math.min(a.x, b.x)) {
      return true;
    }

    double red;
    double blue;

    if (x == a.x) {
      red = Double.MAX_VALUE;
    } else {
      red = (y - a.y) / (x - a.x);
    }
    if (b.x == -a.x) {
      blue = Double.MAX_VALUE;
    } else {
      blue = (b.y - a.y) / (b.x - a.x);
    }
    return red >= blue;
  }

  double getWidth() {
    return Math.abs(maxX - minX);
  }

  double getHeight() {
    return Math.abs(maxY - minY);
  }

  int size() {
    return points.size();
  }

  Point get(int i) {
    return points.get(i);
  }

  double getXMin() {
    return minX;
  }

  double getXMax() {
    return maxX;
  }

  double getYMin() {
    return minY;
  }

  double getYMax() {
    return maxY;
  }

  public final void add(Point point) {
    if (point == null) {
      throw new SofiaRuntimeException("null argument for Point parameter");
    }
    points.add(point);
    if (minX == null || point.x < minX) {
      minX = point.x;
    }
    if (maxX == null || point.x > maxX) {
      maxX = point.x;
    }
    if (minY == null || point.y < minY) {
      minY = point.y;
    }
    if (maxY == null || point.y > maxY) {
      maxY = point.y;
    }
  }

  public Point getFirstPoint() {
    return points.get(0);
  }

  public Point getLasttPoint() {
    return points.get(points.size() - 1);
  }

  boolean notContains(Point p) {
    return !points.contains(p);
  }

  @Override
  public Iterator<Point> iterator() {
    return points.iterator();
  }

  public void add(Polygon p, int start, int end) {
    for (int i = start; i < end; i++) {
      add(p.get(i));
    }
  }

  byte[] toBytes() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    for (Point point : points) {
      byte[] bytes = point.toBytes();
      baos.write(bytes);
    }
    return baos.toByteArray();
  }

  String toJSONArray() {
    StringBuilder sb = new StringBuilder();
    sb.append("[\n");
    for (Point point : points) {
      sb.append(point.toJSONArray()).append(",\n");
    }
    sb = Utils.chop(sb, 2);
    sb.append("\n");
    sb.append("]\n");
    return sb.toString();
  }
}
