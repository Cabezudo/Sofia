package net.cabezudo.sofia.core.users.authorization;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.database.sql.Database;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserManager;
import net.cabezudo.sofia.core.users.autentication.NotLoggedException;
import net.cabezudo.sofia.core.users.permission.Permission;
import net.cabezudo.sofia.core.users.permission.PermissionManager;
import net.cabezudo.sofia.core.users.permission.PermissionsTable;
import net.cabezudo.sofia.core.users.permission.ProfilesPermissionsTable;
import net.cabezudo.sofia.core.users.profiles.PermissionType;
import net.cabezudo.sofia.core.users.profiles.Profile;
import net.cabezudo.sofia.core.users.profiles.ProfileManager;
import net.cabezudo.sofia.core.users.profiles.Profiles;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.09
 */
public class AuthorizationManager {

  private static AuthorizationManager INSTANCE;

  public static AuthorizationManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new AuthorizationManager();
    }
    return INSTANCE;
  }

  private AuthorizationManager() {
  }

  public boolean hasAuthorization(String requestURI, User user, PermissionType permissionType, Site site) throws NotLoggedException, ClusterException {
    if (permissionType == null) {
      Logger.debug("Permission type was not specified for user %s in uri %s on site %s", user, requestURI, site.getBaseDomainName().getName());
      return false;
    }
    Logger.debug("Looking for authorization for uri %s, user %s, permissionType %s and site %s", requestURI, user, permissionType.getName(), site.getBaseDomainName().getName());
    try (Connection connection = Database.getConnection()) {
      Profiles profiles;
      if (user == null) {
        Profile profile = ProfileManager.getInstance().get(connection, "all", site);
        if (profile == null) {
          return false;
        }
        profiles = new Profiles(profile);
      } else {
        if (user.getId() == 1) {
          return true;
        }
        profiles = UserManager.getInstance().getProfiles(user);
      }
      Logger.debug("Check permission for profiles found for user %s.", user);
      for (Profile profile : profiles) {
        Logger.debug("Profile %s.", profile.getName());
        if (PermissionManager.getInstance().has(connection, profile, requestURI, permissionType, site)) {
          Logger.debug("The user has permissions.");
          return true;
        }
      }
      if (PermissionManager.getInstance().isPublic(connection, requestURI, permissionType, site)) {
        Logger.debug("The page is public.");
        return true;
      }
      if (user == null) {
        throw new NotLoggedException();
      }
      Logger.debug("Permission not found.");
      return false;
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public void add(Profiles profiles, String requestURI, PermissionType permissionType, Site site) throws ClusterException {
    for (Profile profile : profiles) {
      add(profile, requestURI, permissionType, site);
    }
  }

  public void add(Profile profile, String requestURI, PermissionType permissionType, Site site) throws ClusterException {
    Logger.debug("Add authorization '%s' for uri %s on site %s,", permissionType.getName(), requestURI, site.getName());

    Permission permission = PermissionManager.getInstance().get(requestURI, site);
    if (permission == null) {
      permission = PermissionManager.getInstance().create(requestURI, site);
    }

    if (!PermissionManager.getInstance().hasRelation(permission, permissionType, site)) {
      PermissionManager.getInstance().add(permission, permissionType, site);
    }

    if (!ProfileManager.getInstance().has(profile, permission, site)) {
      ProfileManager.getInstance().add(profile, permission, site);
    }
  }

  public void delete(String requestURI, Site site) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      delete(connection, requestURI, site);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public void delete(Connection connection, String requestURI, Site site) throws ClusterException {
    String query
            = "DELETE FROM " + ProfilesPermissionsTable.DATABASE_NAME + "." + ProfilesPermissionsTable.NAME + " "
            + "WHERE permission = (SELECT id FROM " + PermissionsTable.DATABASE_NAME + "." + PermissionsTable.NAME + " WHERE uri = ? AND site = ?)";
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, requestURI);
      ps.setInt(2, site.getId());

      ClusterManager.getInstance().executeUpdate(ps);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }
}
