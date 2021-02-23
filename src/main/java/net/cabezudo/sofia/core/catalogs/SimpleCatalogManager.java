package net.cabezudo.sofia.core.catalogs;

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
 * @version 0.01.00, 2020.08.31
 * @param <T>
 */
public abstract class SimpleCatalogManager<T extends CatalogEntry> {

  private final String databaseName;
  private final String tableName;

  protected SimpleCatalogManager(String databaseName, String tableName) {
    this.databaseName = databaseName;
    this.tableName = tableName;
  }

  public T add(String name) throws ClusterException {
    try (Connection connection = Database.getConnection(databaseName)) {
      return add(connection, name);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public T add(Connection connection, String name) throws ClusterException {
    String query = "INSERT INTO " + databaseName + "." + tableName + " (name) VALUES (?)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ps.setString(1, name);
      ClusterManager.getInstance().executeUpdate(ps);
      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return (T) new CatalogEntry(id, name);
      }
      throw new SofiaRuntimeException("Can't get the generated key");
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public T get(int id) throws ClusterException {
    try (Connection connection = Database.getConnection(databaseName)) {
      return get(connection, id);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public T get(Connection connection, int id) throws ClusterException {
    String query = "SELECT id, name FROM " + databaseName + "." + tableName + " WHERE id = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, id);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        return (T) new CatalogEntry(rs.getInt("id"), rs.getString("name"));
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    return null;
  }

  public T get(String name) throws ClusterException {
    try (Connection connection = Database.getConnection(databaseName)) {
      return get(connection, name);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public T get(Connection connection, String name) throws ClusterException {
    String query = "SELECT id, name FROM " + databaseName + "." + tableName + " WHERE name = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, name);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        return (T) new CatalogEntry(rs.getInt("id"), rs.getString("name"));
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    return null;
  }

}
