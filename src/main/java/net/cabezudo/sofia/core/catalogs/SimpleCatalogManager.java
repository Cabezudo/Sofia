package net.cabezudo.sofia.core.catalogs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.31
 * @param <T>
 */
public class SimpleCatalogManager<T extends CatalogEntry> {

  private final String databaseName;
  private final String tableName;

  public SimpleCatalogManager(String databaseName, String tableName) {
    this.databaseName = databaseName;
    this.tableName = tableName;
  }

  public T add(String name) throws SQLException {
    try (Connection connection = Database.getConnection(databaseName)) {
      return add(connection, name);
    }
  }

  public T add(Connection connection, String name) throws SQLException {
    String query = "INSERT INTO " + databaseName + "." + tableName + " (name) VALUES (?)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, name);

      Logger.fine(ps);
      ps.executeUpdate();

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return (T) new CatalogEntry(id, name);
      }
      throw new SofiaRuntimeException("Can't get the generated key");
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
  }

  public T get(int id) throws SQLException {
    try (Connection connection = Database.getConnection(databaseName)) {
      return get(connection, id);
    }
  }

  public T get(Connection connection, int id) throws SQLException {
    String query = "SELECT id, name FROM " + databaseName + "." + tableName + " WHERE id = ?";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setInt(1, id);
      Logger.fine(ps);
      rs = ps.executeQuery();
      if (rs.next()) {
        return (T) new CatalogEntry(rs.getInt("id"), rs.getString("name"));
      }
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
    return null;
  }

  public T get(String name) throws SQLException {
    try (Connection connection = Database.getConnection(databaseName)) {
      return get(connection, name);
    }
  }

  public T get(Connection connection, String name) throws SQLException {
    String query = "SELECT id, name FROM " + databaseName + "." + tableName + " WHERE name = ?";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setString(1, name);
      Logger.fine(ps);
      rs = ps.executeQuery();
      if (rs.next()) {
        return (T) new CatalogEntry(rs.getInt("id"), rs.getString("name"));
      }
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
    return null;
  }

}
