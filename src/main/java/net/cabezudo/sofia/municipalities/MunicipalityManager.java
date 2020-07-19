package net.cabezudo.sofia.municipalities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.states.State;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class MunicipalityManager {

  private static MunicipalityManager INSTANCE;

  public static MunicipalityManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new MunicipalityManager();
    }
    return INSTANCE;
  }

  public Municipality add(State state, String name, User owner) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return add(connection, state, name, owner);
    }
  }

  public Municipality add(Connection connection, State state, String name, User owner) throws SQLException {
    Municipality municipality = get(connection, state, name, owner);
    if (municipality != null) {
      return municipality;
    }
    String query = "INSERT INTO " + MunicipalitiesTable.NAME + " (state, name, owner) VALUES (?, ?, ?)";
    PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    ps.setLong(1, state.getId());
    ps.setString(2, name);
    ps.setInt(3, owner.getId());
    Logger.fine(ps);
    ps.executeUpdate();
    connection.setAutoCommit(true);

    ResultSet rs = ps.getGeneratedKeys();
    if (rs.next()) {
      int id = rs.getInt(1);
      return new Municipality(id, state, name);
    }
    throw new RuntimeException("Can't get the generated key");
  }

  private Municipality get(Connection connection, State state, String name, User owner) throws SQLException {
    String query = "SELECT id, state, name FROM " + MunicipalitiesTable.NAME + " WHERE state = ? AND name = ? AND (owner = ? OR owner = 1)";

    PreparedStatement ps = connection.prepareStatement(query);
    ps.setLong(1, state.getId());
    ps.setString(2, name);
    ps.setInt(3, owner.getId());
    Logger.fine(ps);
    ResultSet rs = ps.executeQuery();

    if (rs.next()) {
      return new Municipality(rs.getInt("id"), state, rs.getString("name"));
    }
    return null;
  }

}
