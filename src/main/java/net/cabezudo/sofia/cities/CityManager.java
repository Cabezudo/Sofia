package net.cabezudo.sofia.cities;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.TreeSet;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.configuration.ConfigurationException;
import net.cabezudo.sofia.core.database.sql.Database;
import net.cabezudo.sofia.core.exceptions.DataConversionException;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.geolocation.Latitude;
import net.cabezudo.sofia.core.geolocation.Longitude;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.countries.Country;
import net.cabezudo.sofia.states.State;
import net.cabezudo.sofia.states.StatesTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class CityManager {

  private static CityManager INSTANCE;

  private CityManager() {
    // Nothing to do
  }

  public static CityManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new CityManager();
    }
    return INSTANCE;
  }

  public City add(State state, String name, Latitude latitude, Longitude longitude, User owner) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return add(connection, state, name, latitude, longitude, owner);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  private City add(Connection connection, State state, String name, Latitude latitude, Longitude longitude, User owner) throws ClusterException {
    ResultSet rs = null;
    String query = "INSERT INTO " + CitiesTable.NAME + " (state, name, latitude, longitude, owner) VALUES (?, ?, ?, ?, ?)";
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      City city = get(connection, state, name, owner);
      if (city != null) {
        return city;
      }
      ps.setInt(1, state.getId());
      ps.setString(2, name);
      ps.setBigDecimal(3, latitude.getValue());
      ps.setBigDecimal(4, longitude.getValue());
      ps.setInt(5, owner.getId());
      ClusterManager.getInstance().executeUpdate(ps);

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        city = new City(id, state, name, latitude, longitude);
        return city;
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    throw new SofiaRuntimeException("Can't get the generated key");
  }

  public City get(Connection connection, State state, String name, User owner) throws ClusterException {
    String query = "SELECT id, state, name, latitude, longitude FROM " + CitiesTable.NAME + " WHERE state = ? AND name = ? AND (owner = ? OR owner = 1)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, state.getId());
      ps.setString(2, name);
      ps.setInt(3, owner.getId());
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        Latitude latitude = new Latitude(rs.getBigDecimal("latitude"));
        Longitude longitude = new Longitude(rs.getBigDecimal("longitude"));
        return new City(rs.getInt("id"), state, rs.getString("name"), latitude, longitude);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    return null;
  }

  Path getDataFile(String twoLetterCountryCode) {
    String citiesDataFile = Configuration.get("cities." + twoLetterCountryCode);
    return Configuration.getCitiesDataPath().resolve(citiesDataFile);
  }

  public void create(MexicoCitiesCreator mexicoCitiesCreator) throws ClusterException, ConfigurationException, DataConversionException {
    mexicoCitiesCreator.create();
  }

  public void get(Country country, Latitude latitude, Longitude longitude, User owner) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      get(connection, country, latitude, longitude, owner);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }

  }

  public void get(Connection connection, Country country, Latitude lat, Longitude lng, User owner) throws ClusterException {
    Set<GeoPointPair> list = new TreeSet<>();

    // TODO add the country to the search
    String query
            = "SELECT s.id AS stateId, s.name AS stateName, c.id AS cityId, c.name AS cityName, latitude, longitude "
            + "FROM " + StatesTable.DATABASE_NAME + "." + StatesTable.NAME + " AS s LEFT JOIN " + CitiesTable.DATABASE_NAME + "." + CitiesTable.NAME + " AS c ON s.id = c.state "
            + "WHERE(owner =  ? OR  owner = 1)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, owner.getId());
      rs = ClusterManager.getInstance().executeQuery(ps);
      while (rs.next()) {
        int stateId = rs.getInt("stateId");
        String stateName = rs.getString("stateName");
        State state = new State(stateId, country, stateName);
        int cityId = rs.getInt("cityId");
        String name = rs.getString("cityName");
        Latitude latitude = new Latitude(rs.getBigDecimal("latitude"));
        Longitude longitude = new Longitude(rs.getBigDecimal("longitude"));
        City city = new City(cityId, state, name, latitude, longitude);
        GeoPointPair geoPointPair = new GeoPointPair(new GeoPoint(lng, lat), city);
        list.add(geoPointPair);
      }
      int count = 0;
      for (GeoPointPair geoPointPair : list) {
        System.out.println(geoPointPair.getDistance() / 1000 + " " + (City) geoPointPair.getSecondPoint());
        count++;
        if (count > 10) {
          break;
        }
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }
}
