package net.cabezudo.sofia.states;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.countries.Country;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class StateManager {

  private static StateManager INSTANCE;

  public static StateManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new StateManager();
    }
    return INSTANCE;
  }

  public State add(Country country, String name) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return add(connection, country, name);
    }
  }

  private State add(Connection connection, Country country, String name) throws SQLException {
    State state = get(connection, country, name);
    if (state != null) {
      return state;
    }
    String query = "INSERT INTO " + StatesTable.NAME + " (country, name) VALUES (?, ?)";
    PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    ps.setLong(1, country.getId());
    ps.setString(2, name);
    Logger.fine(ps);
    ps.executeUpdate();

    ResultSet rs = ps.getGeneratedKeys();
    if (rs.next()) {
      int id = rs.getInt(1);
      return new State(id, country, name);
    }
    throw new RuntimeException("Can't get the generated key");
  }

  private State get(Connection connection, Country country, String name) throws SQLException {
    String query = "SELECT id, country, name FROM " + StatesTable.NAME + " WHERE country = ? AND name = ?";

    PreparedStatement ps = connection.prepareStatement(query);
    ps.setLong(1, country.getId());
    ps.setString(2, name);
    Logger.fine(ps);
    ResultSet rs = ps.executeQuery();

    if (rs.next()) {
      return new State(rs.getInt("id"), country, rs.getString("name"));
    }
    return null;
  }

}
