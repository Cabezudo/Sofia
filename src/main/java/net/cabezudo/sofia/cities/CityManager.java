package net.cabezudo.sofia.cities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.exceptions.InternalRuntimeException;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.logger.Logger;
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

  public City add(State state, String name, User owner) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return add(connection, state, name, owner);
    }
  }

  private City add(Connection connection, State state, String name, User owner) throws SQLException {
    City city = get(connection, state, name, owner);
    if (city != null) {
      return city;
    }
    String query = "INSERT INTO " + CitiesTable.NAME + " (state, name, owner) VALUES (?, ?, ?)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      ps.setInt(1, state.getId());
      ps.setString(2, name);
      ps.setInt(3, owner.getId());
      Logger.fine(ps);
      ps.executeUpdate();
      connection.setAutoCommit(true);

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new City(id, state, name);
      }
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
    throw new InternalRuntimeException("Can't get the generated key");
  }

  private City get(Connection connection, State state, String name, User owner) throws SQLException {
    String query = "SELECT id, state, name FROM " + CitiesTable.NAME + " WHERE state = ? AND name = ? AND (owner = ? OR owner = 1)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setInt(1, state.getId());
      ps.setString(2, name);
      ps.setInt(3, owner.getId());
      Logger.fine(ps);
      rs = ps.executeQuery();

      if (rs.next()) {
        return new City(rs.getInt("id"), state, rs.getString("name"));
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
