package net.cabezudo.sofia.zones;

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
 * @version 0.01.00, 2019.01.28
 */
public class ZoneManager {

  private static ZoneManager INSTANCE;

  public static ZoneManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ZoneManager();
    }
    return INSTANCE;
  }

  public Zone add(String name) throws SQLException {
    try (Connection connection = Database.getConnection()) {
      return add(connection, name);
    }
  }

  public Zone add(Connection connection, String name) throws SQLException {
    Zone zone = get(connection, name);
    if (zone != null) {
      return zone;
    }
    String query = "INSERT INTO " + ZonesTable.NAME + " (name) VALUES (?)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, name);
      Logger.fine(ps);
      ps.executeUpdate();
      connection.setAutoCommit(true);

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new Zone(id, name);
      }
    } finally {
      if (ps != null) {
        ps.close();
      }
      if (rs != null) {
        rs.close();
      }
    }
    throw new SofiaRuntimeException("Can't get the generated key");
  }

  private Zone get(Connection connection, String name) throws SQLException {
    String query = "SELECT id, name FROM " + ZonesTable.NAME + " WHERE name = ?";

    PreparedStatement ps = connection.prepareStatement(query);
    ps.setString(1, name);
    Logger.fine(ps);
    ResultSet rs = ps.executeQuery();

    if (rs.next()) {
      return new Zone(rs.getInt("id"), rs.getString("name"));
    }
    return null;
  }
}
