package net.cabezudo.sofia.core.sites;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.domains.DomainName;
import net.cabezudo.sofia.domains.DomainNameManager;
import net.cabezudo.sofia.domains.DomainNameTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.23
 */
public class SiteManager {

  private static SiteManager INSTANCE;

  public static final int DEFAULT_VERSION = 1;

  public static SiteManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new SiteManager();
    }
    return INSTANCE;
  }

  public Site getById(int id) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return getById(connection, id);
    }
  }

  public Site getById(Connection connection, int id) throws SQLException {
    String query
            = "SELECT s.name AS name, s.host AS baseHost, d.id AS hostId, d.name AS hostname, version "
            + "FROM " + SitesTable.NAME + " AS s "
            + "LEFT JOIN " + DomainNameTable.NAME + " AS d ON s.id = d.siteId "
            + "WHERE s.id = ? ORDER BY hostId";

    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, id);
    Logger.fine(ps);
    ResultSet rs = ps.executeQuery();

    if (rs.next()) {
      String name = rs.getString("name");
      int baseHost = rs.getInt("baseHost");
      int hostId = rs.getInt("hostId");
      String hostName = rs.getString("hostname");
      int version = rs.getInt("version");
      Site site = new Site(id, name, version);
      site.setBaseHost(baseHost);
      site.addHost(hostId, hostName);
      while (rs.next()) {
        hostId = rs.getInt("hostId");
        hostName = rs.getString("hostname");
        site.addHost(hostId, hostName);
      }
      return site;
    }
    return null;
  }

  public Site getByHostName(String hostName) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return getByHostName(connection, hostName);
    }
  }

  public Site getByHostName(Connection connection, String requestHostName) throws SQLException {
    String query
            = "SELECT s.id AS siteId, s.name, s.host AS baseHost, o.id as hostId, o.name AS hostname, version "
            + "FROM " + SitesTable.NAME + " AS s "
            + "LEFT JOIN " + DomainNameTable.NAME + " AS d ON s.id = d.siteId "
            + "LEFT JOIN " + DomainNameTable.NAME + " AS o ON s.id = o.siteId "
            + "WHERE d.name = ? ORDER BY hostId";

    PreparedStatement ps = connection.prepareStatement(query);
    ps.setString(1, requestHostName);
    Logger.fine(ps);
    ResultSet rs = ps.executeQuery();

    if (rs.next()) {
      int id = rs.getInt("siteId");
      String name = rs.getString("name");
      int baseHost = rs.getInt("baseHost");
      int hostId = rs.getInt("hostId");
      String hostName = rs.getString("hostname");
      int version = rs.getInt("version");
      Site site = new Site(id, name, version);
      site.setBaseHost(baseHost);
      site.addHost(hostId, hostName);
      while (rs.next()) {
        hostId = rs.getInt("hostId");
        hostName = rs.getString("hostname");
        site.addHost(hostId, hostName);
      }
      return site;
    }
    return null;
  }

  public Site create(String name, String... domainNames) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      Site site = add(connection, name, domainNames);
      return site;
    }
  }

  public Site add(Connection connection, String name, String... domainNames) throws SQLException {
    connection.setAutoCommit(false);
    // TODO revisar que haya dominios que agregar

    String query = "INSERT INTO " + SitesTable.NAME + " (name) VALUES (?)";
    PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    ps.setString(1, name);
    Logger.fine(ps);
    ps.executeUpdate();

    ResultSet rs = ps.getGeneratedKeys();
    if (rs.next()) {
      int id = rs.getInt(1);

      Site site = new Site(id, name, DEFAULT_VERSION);

      for (int i = 0; i < domainNames.length; i++) {
        DomainName domainName = DomainNameManager.getInstance().add(connection, id, domainNames[i]);
        if (i == 0) {
          site.setBaseHost(domainName);
        }
        site.add(domainName);
      }

      SiteManager.getInstance().update(connection, site);

      connection.setAutoCommit(true);
      return site;
    }
    throw new RuntimeException("Can't get the generated key");
  }

  public Site update(Connection connection, Site site) throws SQLException {
    String query = "UPDATE " + SitesTable.NAME + " SET name = ?, host = ? WHERE id = ?";
    PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    ps.setString(1, site.getName());
    ps.setInt(2, site.getBaseHost().getId());
    ps.setInt(3, site.getId());
    Logger.fine(ps);
    ps.executeUpdate();
    return site;
  }

  public SiteList getAll() throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return getAll(connection);
    }
  }

  public SiteList getAll(Connection connection) throws SQLException {
    String query
            = "SELECT s.id AS siteId, s.name AS name, s.host AS baseHost, d.id AS hostId, d.name AS hostname, version "
            + "FROM " + SitesTable.NAME + " AS s "
            + "LEFT JOIN " + DomainNameTable.NAME + " AS d ON s.id = d.siteId;";

    PreparedStatement ps = connection.prepareStatement(query);
    Logger.fine(ps);
    ResultSet rs = ps.executeQuery();

    SiteList list = new SiteList();

    int lastId = 0;
    Site site = null;

    while (rs.next()) {
      int id = rs.getInt("siteId");
      String name = rs.getString("name");
      int baseHost = rs.getInt("baseHost");
      int hostId = rs.getInt("hostId");
      String hostName = rs.getString("hostname");
      int version = rs.getInt("version");
      if (lastId != id) {
        lastId = id;
        site = new Site(id, name, version);
        list.add(site);
        site.setBaseHost(baseHost);
        site.addHost(hostId, hostName);
      } else {
        hostId = rs.getInt("hostId");
        hostName = rs.getString("hostname");
        if (site == null) {
          throw new NullPointerException("site is null");
        }
        site.addHost(hostId, hostName);
      }
    }
    return list;
  }
}
