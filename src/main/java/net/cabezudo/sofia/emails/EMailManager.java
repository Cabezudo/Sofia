package net.cabezudo.sofia.emails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.18
 */
public class EMailManager {

  private static EMailManager INSTANCE;

  public static EMailManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new EMailManager();
    }
    return INSTANCE;
  }

  public EMail get(String address) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return get(connection, address);
    }
  }

  public EMail get(Connection connection, String address) throws SQLException {

    String query = "SELECT id, personId FROM " + EMailsTable.NAME + " WHERE address = ?";

    PreparedStatement ps = connection.prepareStatement(query);
    ps.setString(1, address);
    Logger.fine(ps);
    ResultSet rs = ps.executeQuery();

    if (rs.next()) {
      int id = rs.getInt("id");
      int personId = rs.getInt("personId");
      return new EMail(id, personId, address);
    }
    return null;
  }

  public EMail get(int id) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return get(connection, id);
    }
  }

  public EMail get(Connection connection, int id) throws SQLException {
    String query = "SELECT id, personId, address FROM " + EMailsTable.NAME + " WHERE id = ?";

    PreparedStatement ps = connection.prepareStatement(query);
    ps.setLong(1, id);
    Logger.fine(ps);
    ResultSet rs = ps.executeQuery();

    if (rs.next()) {
      int personId = rs.getInt("personId");
      String address = rs.getString("address");
      return new EMail(id, personId, address);
    }
    return null;
  }

  public EMails getByPersonId(int personId) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return getByPersonId(connection, personId);
    }
  }

  public EMails getByPersonId(Connection connection, int personId) throws SQLException {

    String query = "SELECT id, address FROM " + EMailsTable.NAME + " WHERE personId = ?";

    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, personId);
    Logger.fine(ps);
    ResultSet rs = ps.executeQuery();

    EMails eMails = new EMails();

    while (rs.next()) {
      int id = rs.getInt("id");
      String address = rs.getString("address");
      eMails.add(new EMail(id, personId, address));
    }
    return eMails;
  }

  public EMail create(int personId, String address) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return create(connection, personId, address);
    }
  }

  public EMail create(Connection connection, int personId, String address) throws SQLException {
    String query = "INSERT INTO " + EMailsTable.NAME + " (personId, address) VALUES (?, ?)";

    PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    ps.setInt(1, personId);
    ps.setString(2, address);
    Logger.fine(ps);
    ps.executeUpdate();

    ResultSet rs = ps.getGeneratedKeys();
    rs.next();
    int id = rs.getInt(1);
    return new EMail(id, personId, address);
  }
}
