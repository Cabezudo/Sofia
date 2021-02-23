package net.cabezudo.sofia.core.users.permission;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.users.profiles.PermissionType;
import net.cabezudo.sofia.core.users.profiles.Profile;
import net.cabezudo.sofia.core.users.profiles.ProfilesTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.17
 */
public class PermissionManager {

  private static PermissionManager INSTANCE;

  public static PermissionManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new PermissionManager();
    }
    return INSTANCE;
  }

  public Permission get(String path, Site site) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, path, site);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Permission get(Connection connection, String path, Site site) throws ClusterException {
    String query
            = "SELECT id FROM " + PermissionsTable.NAME + " WHERE uri = ? AND site = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, path);
      ps.setInt(2, site.getId());
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        int id = rs.getInt("id");
        return new Permission(id, path, site);
      }
      return null;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public Permission get(String path, PermissionType permissionType, Site site) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, path, permissionType, site);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Permission get(Connection connection, String path, PermissionType permissionType, Site site) throws ClusterException {
    String query
            = "SELECT p.id FROM " + PermissionsTable.NAME + " AS p "
            + "LEFT JOIN " + PermissionsPermissionTypesTable.NAME + " AS ppt ON p.id = ppt.permission "
            + "LEFT JOIN " + PermissionTypesTable.NAME + " AS pt ON ppt.permissionType = pt.id "
            + "WHERE uri = ? AND pt.name = ? AND p.site = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, path);
      ps.setInt(2, permissionType.getId());
      ps.setInt(3, site.getId());
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        int id = rs.getInt("id");
        return new Permission(id, path, site);
      }
      return null;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public PermissionType getPermissionType(String name) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return getPermissionType(connection, name);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public PermissionType getPermissionType(Connection connection, String name) throws ClusterException {
    String query = "SELECT id, name FROM " + PermissionTypesTable.NAME + "  WHERE name = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, name);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        int id = rs.getInt("id");
        return new PermissionType(id, name);
      }
      return null;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public PermissionType createPermissionType(String name) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return createPermissionType(connection, name);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public PermissionType createPermissionType(Connection connection, String name) throws ClusterException {
    String query = "INSERT INTO " + PermissionTypesTable.NAME + " (name) VALUES (?)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ps.setString(1, name);
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

  public Permission create(String uri, Site site) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return create(connection, uri, site);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Permission create(Connection connection, String uri, Site site) throws ClusterException {
    String query = "INSERT INTO " + PermissionsTable.NAME + " (uri, site) VALUES (?, ?)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ps.setString(1, uri);
      ps.setInt(2, site.getId());
      ClusterManager.getInstance().executeUpdate(ps);
      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new Permission(id, uri, site);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    throw new SofiaRuntimeException("Can't get the generated key");
  }

  public boolean has(Profile profile, String requestURI, PermissionType permissionType, Site site) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return has(connection, profile, requestURI, permissionType, site);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public boolean has(Connection connection, Profile profile, String requestURI, PermissionType permissionType, Site site) throws ClusterException {

    String query
            = "SELECT p.id AS profileId, p.name AS profileName, p.site AS siteId, ps.id AS permissionId, ps.uri AS permissionURI, pt.id AS permissionTypeId, pt.name AS permissionTypeName "
            + "FROM " + ProfilesTable.NAME + " AS p "
            + "LEFT JOIN " + ProfilesPermissionsTable.NAME + " AS pp ON p.id = pp.profile "
            + "LEFT JOIN " + PermissionsTable.NAME + " AS ps ON pp.permission = ps.id "
            + "LEFT JOIN " + PermissionsPermissionTypesTable.NAME + " AS ppt ON ps.id = ppt.permission "
            + "LEFT JOIN " + PermissionTypesTable.NAME + " AS pt ON ppt.permissionType = pt.id "
            + "WHERE (p.id = ? OR p.name = 'all') AND ps.uri = ? AND pt.id = ? AND p.site = ?";

    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, profile.getId());
      ps.setString(2, requestURI);
      ps.setInt(3, permissionType.getId());
      ps.setInt(4, site.getId());
      rs = ClusterManager.getInstance().executeQuery(ps);
      return rs.next();
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public void add(Permission permission, PermissionType permissionType, Site site) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      add(connection, permission, permissionType, site);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public void add(Connection connection, Permission permission, PermissionType permissionType, Site site) throws ClusterException {
    String query = "INSERT INTO " + PermissionsPermissionTypesTable.NAME + " (permission, permissionType, site) VALUES (?, ?, ?)";
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, permission.getId());
      ps.setInt(2, permissionType.getId());
      ps.setInt(3, site.getId());
      ClusterManager.getInstance().executeUpdate(ps);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public boolean hasRelation(Permission permission, PermissionType permissionType, Site site) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return hasRelation(connection, permission, permissionType, site);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public boolean hasRelation(Connection connection, Permission permission, PermissionType permissionType, Site site) throws ClusterException {
    String query = "SELECT permission, permissionType, site FROM permissionsPermissionTypes WHERE permission = ? AND permissionType = ? AND site = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, permission.getId());
      ps.setInt(2, permissionType.getId());
      ps.setInt(3, site.getId());
      rs = ClusterManager.getInstance().executeQuery(ps);
      return rs.next();
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }
}
