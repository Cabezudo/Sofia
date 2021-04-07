package net.cabezudo.sofia.cities;

import java.util.Objects;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.06
 */
class GeoPointPair implements Comparable<GeoPointPair> {

  private final GeoPointable g1;
  private final GeoPointable g2;

  GeoPointPair(GeoPointable g1, GeoPointable g2) {
    this.g1 = g1;
    this.g2 = g2;
  }

  public GeoPointable getFirstPoint() {
    return g1;
  }

  public GeoPointable getSecondPoint() {
    return g2;
  }

  Integer getDistance() {

    double lat1 = g1.getLatitude().getValue().doubleValue();
    double lon1 = g1.getLongitude().getValue().doubleValue();
    double lat2 = g2.getLatitude().getValue().doubleValue();
    double lon2 = g2.getLongitude().getValue().doubleValue();

    double R = 6371e3; // metres
    double φ1 = lat1 * Math.PI / 180; // φ, λ in radians
    double φ2 = lat2 * Math.PI / 180;
    double Δφ = (lat2 - lat1) * Math.PI / 180;
    double Δλ = (lon2 - lon1) * Math.PI / 180;

    double a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) + Math.cos(φ1) * Math.cos(φ2) * Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    int d = (int) (R * c); // in metres

    return d;
  }

  @Override
  public int compareTo(GeoPointPair o) {
    return Integer.compare(this.getDistance(), o.getDistance());
  }

  @Override
  public int hashCode() {
    return this.getDistance().hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    if (getClass() != o.getClass()) {
      return false;
    }
    final GeoPointPair geoPointPair = (GeoPointPair) o;
    return Objects.equals(this.getDistance(), geoPointPair.getDistance());
  }

}
