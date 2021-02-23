package net.cabezudo.sofia.states;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.countries.Country;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class StateManager {

  private static StateManager instance;

  public static StateManager getInstance() {
    if (instance == null) {
      instance = new StateManager();
    }
    return instance;
  }

  public State add(Country country, String name) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return add(connection, country, name);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  private State add(Connection connection, Country country, String name) throws ClusterException {
    ResultSet rs = null;
    State state = get(connection, country, name);
    if (state != null) {
      return state;
    }
    String query = "INSERT INTO " + StatesTable.NAME + " (country, name) VALUES (?, ?)";
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ps.setLong(1, country.getId());
      ps.setString(2, name);
      ClusterManager.getInstance().executeUpdate(ps);
      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new State(id, country, name);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    throw new SofiaRuntimeException("Can't get the generated key");
  }

  private State get(Connection connection, Country country, String name) throws ClusterException {
    String query = "SELECT id, country, name FROM " + StatesTable.NAME + " WHERE country = ? AND name = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setLong(1, country.getId());
      ps.setString(2, name);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        return new State(rs.getInt("id"), country, rs.getString("name"));
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    return null;
  }

}
