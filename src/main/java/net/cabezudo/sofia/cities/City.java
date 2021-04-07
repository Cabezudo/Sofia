package net.cabezudo.sofia.cities;

import net.cabezudo.sofia.core.geolocation.Latitude;
import net.cabezudo.sofia.core.geolocation.Longitude;
import net.cabezudo.sofia.states.State;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class City implements GeoPointable {

  private final int id;
  private final State state;
  private final String name;
  private final Latitude latitude;
  private final Longitude longitude;

  public City(int id, State state, String name, Latitude latitude, Longitude longitude) {
    this.id = id;
    this.state = state;
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  @Override
  public String toString() {
    return "[id = " + getId() + ", state = " + getState() + ", name = " + getName() + ", latitude = " + getLatitude() + ", longitude = " + getLongitude() + "]";
  }

  public int getId() {
    return id;
  }

  public State getState() {
    return state;
  }

  public String getName() {
    return name;
  }

  @Override
  public Latitude getLatitude() {
    return latitude;
  }

  @Override
  public Longitude getLongitude() {
    return longitude;
  }

}
