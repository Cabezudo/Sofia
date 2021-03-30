package net.cabezudo.sofia.core.users.permission;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.database.sql.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.users.profiles.PermissionType;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.17
 */
public class PermissionTypeManager {

  private static PermissionTypeManager INSTANCE;

  public static PermissionTypeManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new PermissionTypeManager();
    }
    return INSTANCE;
  }

  // TODO colocar una cache aquí.
  public PermissionType get(String name, Site site) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, name, site);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  private PermissionType get(Connection connection, String name, Site site) throws ClusterException {
    String query = "SELECT id, name FROM " + PermissionTypesTable.NAME + " WHERE name = ? AND site = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, name);
      ps.setInt(2, site.getId());
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        return new PermissionType(rs.getInt("id"), rs.getString("name"));
      }
      return null;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public PermissionType create(String name, Site site) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return create(connection, name, site);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public PermissionType create(Connection connection, String name, Site site) throws ClusterException {
    Logger.debug("Create permission %s of site %s.", name, site.getId());
    String query = "INSERT INTO " + PermissionTypesTable.NAME + " (name, site) VALUES (?, ?)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ps.setString(1, name);
      ps.setInt(2, site.getId());
      ClusterManager.getInstance().executeUpdate(ps);
      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new PermissionType(id, name);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    throw new SofiaRuntimeException("Can't get the generated key");
  }
}
