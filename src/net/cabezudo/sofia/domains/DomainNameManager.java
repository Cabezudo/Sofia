package net.cabezudo.sofia.domains;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class DomainNameManager {

  private static DomainNameManager INSTANCE;

  public static DomainNameManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new DomainNameManager();
    }
    return INSTANCE;
  }

  public DomainNameList get(Site site) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return get(connection, site);
    }
  }

  public DomainNameList get(Connection connection, Site site) throws SQLException {
    String query = "SELECT id, name FROM " + DomainNameTable.NAME + " WHERE siteId = ?";

    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, site.getId());
    Logger.fine(ps);
    ResultSet rs = ps.executeQuery();

    DomainNameList list = new DomainNameList();

    while (rs.next()) {
      Integer id = rs.getInt(1);
      String name = rs.getString(2);
      DomainName domainName = new DomainName(id, name);
      list.add(domainName);
    }
    return list;
  }

  public DomainName add(Connection connection, int siteId, String domainName) throws SQLException {

    String query = "INSERT INTO " + DomainNameTable.NAME + " (siteId, name) VALUES (?, ?)";
    PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    ps.setInt(1, siteId);
    ps.setString(2, domainName);
    Logger.fine(ps);
    ps.executeUpdate();
    connection.setAutoCommit(true);

    ResultSet rs = ps.getGeneratedKeys();
    if (rs.next()) {
      int id = rs.getInt(1);
      return new DomainName(id, domainName);
    }
    throw new RuntimeException("Can't get the generated key");
  }

  public DomainName get(int hostId) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return get(connection, hostId);
    }
  }

  public DomainName get(Connection connection, int hostId) throws SQLException {
    String query = "SELECT id, name FROM " + DomainNameTable.NAME + " WHERE id = ?";

    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, hostId);
    Logger.fine(ps);
    ResultSet rs = ps.executeQuery();

    if (rs.next()) {
      Integer id = rs.getInt(1);
      String name = rs.getString(2);
      DomainName domainName = new DomainName(id, name);
      return domainName;
    }
    return null;
  }
}
