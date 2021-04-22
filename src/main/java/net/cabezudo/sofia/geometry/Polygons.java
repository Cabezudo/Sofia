package net.cabezudo.sofia.geometry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.cabezudo.sofia.core.Utils;

public class Polygons implements Iterable<Polygon> {

  private final List<Polygon> list = new ArrayList<>();
  private double width;
  private double height;
  private Double xMin = null;
  private Double xMax = null;
  private Double yMin = null;
  private Double yMax = null;

  public Polygons() {
  }

  public void add(Polygon polygon) {
    list.add(polygon);
    if (polygon.getWidth() > width) {
      width = polygon.getWidth();
    }
    if (xMin == null || polygon.getXMin() < xMin) {
      xMin = polygon.getXMin();
    }
    if (xMax == null || polygon.getXMax() > xMax) {
      xMax = polygon.getXMax();
    }
    if (polygon.getHeight() > height) {
      height = polygon.getHeight();
    }
    if (yMin == null || polygon.getYMin() < yMin) {
      yMin = polygon.getYMin();
    }
    if (yMax == null || polygon.getYMax() > yMax) {
      yMax = polygon.getYMax();
    }
  }

  double getWidth() {
    return width;
  }

  double getHeight() {
    return height;
  }

  @Override
  public Iterator<Polygon> iterator() {
    return list.iterator();
  }

  double getMinX() {
    return xMin;
  }

  double getMaxX() {
    return xMax;
  }

  double getMinY() {
    return yMin;
  }

  double getMaxY() {
    return yMax;
  }

  public boolean isInside(Point point) {
    for (Polygon polygon : list) {
      boolean isInside = polygon.isInside(point);
      if (isInside) {
        return true;
      }
    }
    return false;
  }

  public int size() {
    return list.size();
  }

  public Polygon get(int i) {
    return list.get(i);
  }

  byte[] toBytes() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    for (Polygon polygon : list) {
      byte[] bytes = polygon.toBytes();
      baos.write(bytes.length);
      baos.write(bytes);
    }
    return baos.toByteArray();
  }

  String toJSONArray() {
    StringBuilder sb = new StringBuilder();
    sb.append("[\n");
    for (Polygon polygon : list) {
      String jsonData = polygon.toJSONArray();
      sb.append(jsonData);
      sb.append(",\n");
    }
    sb = Utils.chop(sb, 2);
    sb.append("]\n");
    return sb.toString();
  }
}
