package net.cabezudo.sofia.postalcodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.settlements.Settlement;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class PostalCodeManager {

  private static PostalCodeManager INSTANCE;

  public static PostalCodeManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new PostalCodeManager();
    }
    return INSTANCE;
  }

  public PostalCode add(Settlement settlement, int postalCode, User owner) throws SQLException {
    try (Connection connection = Database.getConnection()) {
      return add(connection, settlement, postalCode, owner);
    }
  }

  public PostalCode add(Connection connection, Settlement settlement, int postalCode, User owner) throws SQLException {
    PostalCode state = get(connection, settlement, postalCode, owner);
    if (state != null) {
      return state;
    }
    String query = "INSERT INTO " + PostalCodesTable.NAME + " (settlement, postalCode, owner) VALUES (?, ?, ?)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ps.setInt(1, settlement.getId());
      ps.setInt(2, postalCode);
      ps.setInt(3, owner.getId());
      Logger.fine(ps);
      ps.executeUpdate();
      connection.setAutoCommit(true);

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new PostalCode(id, settlement, postalCode);
      }
      throw new SofiaRuntimeException("Can't get the generated key");
    } finally {
      if (rs != null) {
        rs.close();
      }
    }
  }

  private PostalCode get(Connection connection, Settlement settlement, int postalCode, User owner) throws SQLException {
    String query = "SELECT id, settlement, postalCode FROM " + PostalCodesTable.NAME + " WHERE settlement = ? AND postalCode = ? AND (owner = ? OR owner = 1)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, settlement.getId());
      ps.setInt(2, postalCode);
      ps.setInt(3, owner.getId());
      Logger.fine(ps);
      rs = ps.executeQuery();

      if (rs.next()) {
        return new PostalCode(rs.getInt("id"), settlement, postalCode);
      }
      return null;
    } finally {
      if (rs != null) {
        rs.close();
      }
    }
  }
}
