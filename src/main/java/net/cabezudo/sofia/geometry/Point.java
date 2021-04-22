package net.cabezudo.sofia.geometry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Point implements Comparable<Point> {

  public final double x;
  public final double y;

  public Point(Double x, Double y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (o instanceof Point) {
      Point p = (Point) o;
      return (x == p.x && y == p.y);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 11 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
    hash = 11 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
    return hash;
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }

  @Override
  public int compareTo(Point o) {
    if (x == o.x) {
      return Double.compare(y, o.y);
    }
    return Double.compare(x, o.x);
  }

  byte[] toBytes() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    baos.write(toByteArray(x));
    baos.write(toByteArray(y));
    return baos.toByteArray();
  }

  private byte[] toByteArray(double number) {
    ByteBuffer byteBuffer = ByteBuffer.allocate(Double.BYTES);
    byteBuffer.putDouble(number);
    return byteBuffer.array();
  }

  String toJSONArray() {
    return x + ", " + y;
  }

}
