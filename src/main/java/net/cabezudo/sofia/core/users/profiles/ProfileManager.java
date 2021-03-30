package net.cabezudo.sofia.core.users.profiles;

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
import net.cabezudo.sofia.core.users.permission.Permission;
import net.cabezudo.sofia.core.users.permission.Permissions;
import net.cabezudo.sofia.core.users.permission.PermissionsTable;
import net.cabezudo.sofia.core.users.permission.ProfilesPermissionsTable;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.16
 */
public class ProfileManager {

  private static final ProfileManager INSTANCE = new ProfileManager();

  public static ProfileManager getInstance() {
    return INSTANCE;
  }

  public Profiles createFromNames(String[] ps, Site site) throws ClusterException {
    Profiles profiles = new Profiles();
    try (Connection connection = Database.getConnection()) {
      for (String s : ps) {
        String name = s.trim();
        Logger.debug("Profile name to search or create: " + name);
        Profile profile = get(connection, name, site);
        Logger.debug("Profile found: " + profile);
        if (profile == null) {
          Logger.debug("Create profile using name: " + name);
          profile = create(connection, name, site);
        }
        profiles.add(profile);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
    return profiles;
  }

  public Profile create(Connection connection, String name, Site site) throws ClusterException {
    String query = "INSERT INTO " + ProfilesTable.NAME + " (name, site) VALUES (?, ?)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ps.setString(1, name);
      ps.setInt(2, site.getId());
      ClusterManager.getInstance().executeUpdate(ps);
      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new Profile(id, name, site);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    throw new SofiaRuntimeException("Can't get the generated key");
  }

  public Profile get(String profileName, Site site) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, profileName, site);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Profile get(Connection connection, String name, Site site) throws ClusterException {
    String query = "SELECT `id`, `name`, `site` FROM " + ProfilesTable.NAME + " WHERE name = ? AND site = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, name);
      ps.setInt(2, site.getId());
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        int id = rs.getInt("id");
        return new Profile(id, name, site);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    return null;
  }

  public boolean has(Profile profile, Permission permission, Site site) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return has(connection, profile, permission, site);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public boolean has(Connection connection, Profile profile, Permission permission, Site site) throws ClusterException {
    String query = "SELECT `profile`, `permission`, `site` FROM " + ProfilesPermissionsTable.NAME + " WHERE profile = ? AND permission = ? AND site = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, profile.getId());
      ps.setInt(2, permission.getId());
      ps.setInt(3, site.getId());
      rs = ClusterManager.getInstance().executeQuery(ps);
      return rs.next();
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public void add(Profile profile, Permission permission, Site site) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      add(connection, profile, permission, site);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public void add(Connection connection, Profile profile, Permission permission, Site site) throws ClusterException {
    String query = "INSERT INTO " + ProfilesPermissionsTable.NAME + " (profile, permission, site) VALUES (?, ?, ?)";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setInt(1, profile.getId());
      ps.setInt(2, permission.getId());
      ps.setInt(3, site.getId());
      ClusterManager.getInstance().executeUpdate(ps);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Permissions getPermissions(Connection connection, Profile profile, Site site) throws ClusterException {
    String query
            = "SELECT p.id, p.uri FROM " + ProfilesPermissionsTable.NAME + " AS pp "
            + "LEFT JOIN " + PermissionsTable.NAME + " AS p ON pp.profile = p.id "
            + "WHERE pp.profile = ? AND pp.site = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, profile.getId());
      ps.setInt(2, site.getId());
      rs = ClusterManager.getInstance().executeQuery(ps);
      Permissions permissions = new Permissions();
      while (rs.next()) {
        int id = rs.getInt("id");
        String uri = rs.getString("uri");
        Permission permission = new Permission(id, uri, site);
        permissions.add(permission);
      }
      return permissions;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }
}
