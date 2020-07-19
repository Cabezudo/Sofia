package net.cabezudo.sofia.settlements;

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
 * @version 0.01.00, 2019.01.28
 */
public class SettlementTypeManager {

  private static SettlementTypeManager INSTANCE;

  public static SettlementTypeManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new SettlementTypeManager();
    }
    return INSTANCE;
  }

  public SettlementType add(String name) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return add(connection, name);
    }
  }

  public SettlementType add(Connection connection, String name) throws SQLException {
    SettlementType settlementType = get(connection, name);
    if (settlementType != null) {
      return settlementType;
    }
    String query = "INSERT INTO " + SettlementTypesTable.NAME + " (name) VALUES (?)";
    PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    ps.setString(1, name);
    Logger.fine(ps);
    ps.executeUpdate();
    connection.setAutoCommit(true);

    ResultSet rs = ps.getGeneratedKeys();
    if (rs.next()) {
      int id = rs.getInt(1);
      return new SettlementType(id, name);
    }
    throw new RuntimeException("Can't get the generated key");
  }

  private SettlementType get(Connection connection, String name) throws SQLException {
    String query = "SELECT id, name FROM " + SettlementTypesTable.NAME + " WHERE name = ?";

    PreparedStatement ps = connection.prepareStatement(query);
    ps.setString(1, name);
    Logger.fine(ps);
    ResultSet rs = ps.executeQuery();

    if (rs.next()) {
      return new SettlementType(rs.getInt("id"), rs.getString("name"));
    }
    return null;
  }
}
