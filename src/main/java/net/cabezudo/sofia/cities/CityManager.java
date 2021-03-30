package net.cabezudo.sofia.cities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.database.sql.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.states.State;

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

  public City add(State state, String name, User owner) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return add(connection, state, name, owner);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  private City add(Connection connection, State state, String name, User owner) throws ClusterException {
    ResultSet rs = null;
    String query = "INSERT INTO " + CitiesTable.NAME + " (state, name, owner) VALUES (?, ?, ?)";
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      City city = get(connection, state, name, owner);
      if (city != null) {
        return city;
      }
      ps.setInt(1, state.getId());
      ps.setString(2, name);
      ps.setInt(3, owner.getId());
      ClusterManager.getInstance().executeUpdate(ps);

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        city = new City(id, state, name);
        return city;
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    throw new SofiaRuntimeException("Can't get the generated key");
  }

  private City get(Connection connection, State state, String name, User owner) throws ClusterException {
    String query = "SELECT id, state, name FROM " + CitiesTable.NAME + " WHERE state = ? AND name = ? AND (owner = ? OR owner = 1)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, state.getId());
      ps.setString(2, name);
      ps.setInt(3, owner.getId());
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        return new City(rs.getInt("id"), state, rs.getString("name"));
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    return null;
  }

}
