package net.cabezudo.sofia.zones;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class ZoneManager {

  private static ZoneManager instance;

  public static ZoneManager getInstance() {
    if (instance == null) {
      instance = new ZoneManager();
    }
    return instance;
  }

  public Zone add(String name) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return add(connection, name);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Zone add(Connection connection, String name) throws ClusterException {
    Zone zone = get(connection, name);
    if (zone != null) {
      return zone;
    }
    String query = "INSERT INTO " + ZonesTable.NAME + " (name) VALUES (?)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ps.setString(1, name);
      ClusterManager.getInstance().executeUpdate(ps);
      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new Zone(id, name);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    throw new SofiaRuntimeException("Can't get the generated key");
  }

  private Zone get(Connection connection, String name) throws ClusterException {
    String query = "SELECT id, name FROM " + ZonesTable.NAME + " WHERE name = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, name);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        return new Zone(rs.getInt("id"), rs.getString("name"));
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    return null;
  }
}
