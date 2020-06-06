package net.cabezudo.sofia.core.users.permission;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.users.profiles.PermissionType;

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
  public PermissionType get(String name, Site site) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return get(connection, name, site);
    }
  }

  private PermissionType get(Connection connection, String name, Site site) throws SQLException {
    String query = "SELECT id, name FROM " + PermissionTypesTable.NAME + " WHERE name = ? AND site = ?";

    PreparedStatement ps = connection.prepareStatement(query);
    ps.setString(1, name);
    ps.setInt(2, site.getId());
    Logger.fine(ps);
    ResultSet rs = ps.executeQuery();

    if (rs.next()) {
      return new PermissionType(rs.getInt("id"), rs.getString("name"));
    }
    return null;
  }

  public PermissionType create(String name, Site site) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return create(connection, name, site);
    }
  }

  public PermissionType create(Connection connection, String name, Site site) throws SQLException {
    Logger.debug("Create permission %s of site %s.", name, site.getId());
    String query = "INSERT INTO " + PermissionTypesTable.NAME + " (name, site) VALUES (?, ?)";
    PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    ps.setString(1, name);
    ps.setInt(2, site.getId());
    Logger.fine(ps);
    ps.executeUpdate();

    ResultSet rs = ps.getGeneratedKeys();
    if (rs.next()) {
      int id = rs.getInt(1);
      return new PermissionType(id, name);
    }
    throw new RuntimeException("Can't get the generated key");
  }
}
