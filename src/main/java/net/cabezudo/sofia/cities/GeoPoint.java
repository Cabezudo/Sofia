package net.cabezudo.sofia.cities;

import net.cabezudo.sofia.core.geolocation.Latitude;
import net.cabezudo.sofia.core.geolocation.Longitude;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.06
 */
public class GeoPoint implements GeoPointable {

  private final Longitude longitude;
  private final Latitude latitude;

  public GeoPoint(Longitude longitude, Latitude latitude) {
    this.longitude = longitude;
    this.latitude = latitude;
  }

  @Override
  public Longitude getLongitude() {
    return longitude;
  }

  @Override
  public Latitude getLatitude() {
    return latitude;
  }

}
