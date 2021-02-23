package net.cabezudo.sofia.settlements;

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
public class SettlementTypeManager {

  private static SettlementTypeManager instance;

  public static SettlementTypeManager getInstance() {
    if (instance == null) {
      instance = new SettlementTypeManager();
    }
    return instance;
  }

  public SettlementType add(String name) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return add(connection, name);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public SettlementType add(Connection connection, String name) throws ClusterException {
    SettlementType settlementType = get(connection, name);
    if (settlementType != null) {
      return settlementType;
    }
    String query = "INSERT INTO " + SettlementTypesTable.NAME + " (name) VALUES (?)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ps.setString(1, name);
      ClusterManager.getInstance().executeUpdate(ps);
      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new SettlementType(id, name);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    throw new SofiaRuntimeException("Can't get the generated key");
  }

  private SettlementType get(Connection connection, String name) throws ClusterException {
    String query = "SELECT id, name FROM " + SettlementTypesTable.NAME + " WHERE name = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, name);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        return new SettlementType(rs.getInt("id"), rs.getString("name"));
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    return null;
  }
}
