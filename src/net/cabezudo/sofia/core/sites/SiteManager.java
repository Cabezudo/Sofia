package net.cabezudo.sofia.core.sites;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import net.cabezudo.sofia.core.QueryHelper;
import net.cabezudo.sofia.core.api.options.OptionValue;
import net.cabezudo.sofia.core.api.options.list.Filters;
import net.cabezudo.sofia.core.api.options.list.Limit;
import net.cabezudo.sofia.core.api.options.list.Offset;
import net.cabezudo.sofia.core.api.options.list.Sort;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.hosts.Host;
import net.cabezudo.sofia.hosts.HostManager;
import net.cabezudo.sofia.hosts.HostsTable;

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
        + "LEFT JOIN " + HostsTable.NAME + " AS d ON s.id = d.siteId "
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
        + "LEFT JOIN " + HostsTable.NAME + " AS d ON s.id = d.siteId "
        + "LEFT JOIN " + HostsTable.NAME + " AS o ON s.id = o.siteId "
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

  public Site create(String name, String... hosts) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      Site site = add(connection, name, hosts);
      return site;
    }
  }

  public Site add(Connection connection, String name, String... hosts) throws SQLException {
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

      for (int i = 0; i < hosts.length; i++) {
        String domainNameString = hosts[i];
        if (domainNameString.isEmpty()) {
          continue;
        }
        Host host = HostManager.getInstance().add(connection, id, domainNameString);
        if (i == 0) {
          site.setBaseHost(host);
        }
        site.add(host);
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

  public SiteList list() throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return list(connection);
    }
  }

  public SiteList list(Connection connection) throws SQLException {
    String query
        = "SELECT s.id AS siteId, s.name AS name, s.host AS baseHost, d.id AS hostId, d.name AS hostname, version "
        + "FROM " + SitesTable.NAME + " AS s "
        + "LEFT JOIN " + HostsTable.NAME + " AS d ON s.id = d.siteId;";

    PreparedStatement ps = connection.prepareStatement(query);
    Logger.fine(ps);
    ResultSet rs = ps.executeQuery();
    SiteList list = new SiteList(0, 0);

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

  SiteList list(Filters filters, Sort sort, Offset offset, Limit limit, User owner) throws SQLException {
    Logger.fine("Site list");

    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      String where = getSiteWhere(filters);

      long sqlOffsetValue = 0;
      if (offset != null) {
        sqlOffsetValue = offset.getValue();
      }
      long sqlLimitValue = SiteList.MAX;
      if (limit != null) {
        sqlLimitValue = limit.getValue();
      }

      String sqlSort = QueryHelper.getOrderString(sort, "name", new String[]{"id", "name"});

      String sqlLimit = " LIMIT " + sqlOffsetValue + ", " + sqlLimitValue;

      String query = "SELECT id, name, version FROM " + SitesTable.NAME + where + sqlSort + sqlLimit;
      PreparedStatement ps = connection.prepareStatement(query);
      setSiteFilters(filters, ps);

      Logger.fine(ps);
      ResultSet rs = ps.executeQuery();

      SiteList list = new SiteList(offset == null ? 0 : offset.getValue(), limit == null ? 0 : limit.getValue());
      while (rs.next()) {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        int version = rs.getInt("version");

        Site site = new Site(id, name, version);

        list.add(site);
      }

      query = "SELECT FOUND_ROWS() AS total";
      ps = connection.prepareStatement(query);
      Logger.fine(ps);
      rs = ps.executeQuery();
      if (!rs.next()) {
        throw new RuntimeException("The select to count the number of sites fail.");
      }
      int total = rs.getInt("total");

      list.setTotal(total);

      return list;
    }
  }

  int getTotal(Filters filters, Sort sort, Offset offset, Limit limit, User owner) throws SQLException {
    Logger.fine("Site list total");

    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      String where = getSiteWhere(filters);

      long sqlOffsetValue = 0;
      if (offset != null) {
        sqlOffsetValue = offset.getValue();
      }
      long sqlLimitValue = SiteList.MAX;
      if (limit != null) {
        sqlLimitValue = limit.getValue();
      }

      String sqlSort = QueryHelper.getOrderString(sort, "name", new String[]{"id", "name"});

      String sqlLimit = " LIMIT " + sqlOffsetValue + ", " + sqlLimitValue;

      String query = "SELECT count(*) AS total FROM " + SitesTable.NAME + where + sqlSort + sqlLimit;
      PreparedStatement ps = connection.prepareStatement(query);
      setSiteFilters(filters, ps);

      Logger.fine(ps);
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        int total = rs.getInt("total");
        return total;
      } else {
        throw new RuntimeException("Column expected.");
      }
    }
  }

  private String getSiteWhere(Filters filter) {
    String where = " WHERE 1 = 1";
    if (filter != null) {
      List<OptionValue> values = filter.getValues();
      for (OptionValue value : values) {
        if (value.isPositive()) {
          where += " AND (name LIKE ?)";
        } else {
          where += " AND name NOT LIKE ?";
        }
      }
    }
    return where;
  }

  private void setSiteFilters(Filters filter, PreparedStatement ps) throws SQLException {
    if (filter != null) {
      int i = 1;
      for (OptionValue ov : filter.getValues()) {
        ps.setString(i, "%" + ov.getValue() + "%");
        i++;
      }
    }
  }

  int getHostsTotal(Site site, Filters filters, Sort sort, Offset offset, Limit limit, User owner) throws SQLException {
    Logger.fine("Site host list total");

    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      String where = getHostWhere(filters);

      long sqlOffsetValue = 0;
      if (offset != null) {
        sqlOffsetValue = offset.getValue();
      }
      long sqlLimitValue = SiteList.MAX;
      if (limit != null) {
        sqlLimitValue = limit.getValue();
      }

      String sqlSort = QueryHelper.getOrderString(sort, "name", new String[]{"id", "name"});

      String sqlLimit = " LIMIT " + sqlOffsetValue + ", " + sqlLimitValue;

      String query = "SELECT count(*) AS total FROM " + HostsTable.NAME + where + sqlSort + sqlLimit;
      PreparedStatement ps = connection.prepareStatement(query);

      ps.setInt(1, site.getId());

      setHostFilters(filters, ps);

      Logger.fine(ps);
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        int total = rs.getInt("total");
        return total;
      } else {
        throw new RuntimeException("Column expected.");
      }
    }
  }

  private String getHostWhere(Filters filter) {
    String where = " WHERE siteId = ?";
    if (filter != null) {
      List<OptionValue> values = filter.getValues();
      for (OptionValue value : values) {
        if (value.isPositive()) {
          where += " AND (name LIKE ?)";
        } else {
          where += " AND name NOT LIKE ?";
        }
      }
    }
    return where;
  }

  private void setHostFilters(Filters filter, PreparedStatement ps) throws SQLException {
    if (filter != null) {
      int i = 2;
      for (OptionValue ov : filter.getValues()) {
        ps.setString(i, "%" + ov.getValue() + "%");
        i++;
      }
    }
  }

  HostList listHosts(Site site, Filters filters, Sort sort, Offset offset, Limit limit, User owner) throws SQLException {
    Logger.fine("Site list");

    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      String where = getHostWhere(filters);

      long sqlOffsetValue = 0;
      if (offset != null) {
        sqlOffsetValue = offset.getValue();
      }
      long sqlLimitValue = SiteList.MAX;
      if (limit != null) {
        sqlLimitValue = limit.getValue();
      }

      String sqlSort = QueryHelper.getOrderString(sort, "name", new String[]{"id", "name"});

      String sqlLimit = " LIMIT " + sqlOffsetValue + ", " + sqlLimitValue;

      String query = "SELECT id, name FROM " + HostsTable.NAME + where + sqlSort + sqlLimit;
      PreparedStatement ps = connection.prepareStatement(query);
      ps.setInt(1, site.getId());
      setHostFilters(filters, ps);

      Logger.fine(ps);
      ResultSet rs = ps.executeQuery();

      HostList list = new HostList(offset == null ? 0 : offset.getValue(), limit == null ? 0 : limit.getValue());
      while (rs.next()) {
        int id = rs.getInt("id");
        String name = rs.getString("name");

        Host host = new Host(id, name);

        list.add(host);
      }

      query = "SELECT FOUND_ROWS() AS total";
      ps = connection.prepareStatement(query);
      Logger.fine(ps);
      rs = ps.executeQuery();
      if (!rs.next()) {
        throw new RuntimeException("The select to count the number of sites fail.");
      }
      int total = rs.getInt("total");

      list.setTotal(total);

      return list;
    }
  }
}
