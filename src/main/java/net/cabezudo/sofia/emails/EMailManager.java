package net.cabezudo.sofia.emails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.database.sql.Database;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.18
 */
public class EMailManager {

  private static EMailManager instance;

  public static EMailManager getInstance() {
    if (instance == null) {
      instance = new EMailManager();
    }
    return instance;
  }

  public EMail get(String address) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, address);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public EMail get(Connection connection, String address) throws ClusterException {

    String query = "SELECT id, personId FROM " + EMailsTable.DATABASE_NAME + "." + EMailsTable.NAME + " WHERE address = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, address);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        int id = rs.getInt("id");
        int personId = rs.getInt("personId");
        return new EMail(id, personId, address);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    return null;
  }

  public EMail get(int id) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, id);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public EMail get(Connection connection, int id) throws ClusterException {
    String query = "SELECT id, personId, address FROM " + EMailsTable.DATABASE_NAME + "." + EMailsTable.NAME + " WHERE id = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setLong(1, id);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        int personId = rs.getInt("personId");
        String address = rs.getString("address");
        return new EMail(id, personId, address);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    return null;
  }

  public EMails getByPersonId(int personId) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return getByPersonId(connection, personId);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public EMails getByPersonId(Connection connection, int personId) throws ClusterException {

    String query = "SELECT id, address FROM " + EMailsTable.DATABASE_NAME + "." + EMailsTable.NAME + " WHERE personId = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, personId);
      rs = ClusterManager.getInstance().executeQuery(ps);
      EMails eMails = new EMails();
      while (rs.next()) {
        int id = rs.getInt("id");
        String address = rs.getString("address");
        eMails.add(new EMail(id, personId, address));
      }
      return eMails;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public EMail create(int personId, String address) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return create(connection, personId, address);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public EMail create(Connection connection, int personId, String address) throws ClusterException {
    String query = "INSERT INTO " + EMailsTable.DATABASE_NAME + "." + EMailsTable.NAME + " (personId, address) VALUES (?, ?)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ps.setInt(1, personId);
      ps.setString(2, address);
      ClusterManager.getInstance().executeUpdate(ps);
      rs = ps.getGeneratedKeys();
      rs.next();
      int id = rs.getInt(1);
      return new EMail(id, personId, address);
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }
}
