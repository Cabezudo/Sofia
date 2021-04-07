package net.cabezudo.sofia.cities;

import net.cabezudo.sofia.core.geolocation.Latitude;
import net.cabezudo.sofia.core.geolocation.Longitude;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.06
 */
public interface GeoPointable {

  Latitude getLatitude();

  Longitude getLongitude();
}
