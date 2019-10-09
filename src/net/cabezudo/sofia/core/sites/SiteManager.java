package net.cabezudo.sofia.core.sites;

import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import net.cabezudo.sofia.core.sites.domainname.DomainName;
import net.cabezudo.sofia.core.sites.domainname.DomainNameList;
import net.cabezudo.sofia.core.sites.domainname.DomainNameManager;
import net.cabezudo.sofia.core.sites.domainname.DomainNamesTable;

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
        = "SELECT s.name AS name, s.domainName AS baseDomainNameId, d.id AS domainNameId, d.name AS domainName, version "
        + "FROM " + SitesTable.NAME + " AS s "
        + "LEFT JOIN " + DomainNamesTable.NAME + " AS d ON s.id = d.siteId "
        + "WHERE s.id = ? ORDER BY domainName";

    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, id);
    Logger.fine(ps);
    ResultSet rs = ps.executeQuery();

    String name = null;
    int baseDomainNameId = 0;
    int version = 0;
    DomainName baseDomainName = null;
    DomainNameList domainNameList = new DomainNameList();

    while (rs.next()) {
      if (name == null) {
        name = rs.getString("name");
        baseDomainNameId = rs.getInt("baseDomainNameId");
        version = rs.getInt("version");
      }

      int domainNameId = rs.getInt("domainNameId");
      String domainNameName = rs.getString("domainName");
      DomainName domainName = new DomainName(domainNameId, id, domainNameName);
      if (domainNameId == baseDomainNameId) {
        baseDomainName = domainName;
      } else {
        domainNameList.add(domainName);
      }
    }
    Site site = new Site(id, name, baseDomainName, domainNameList, version);
    System.out.println("2: " + site);
    return site;
  }

  public Site getByHostName(String domainName) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return getByDomainNameName(connection, domainName);
    }
  }

  public Site getByDomainNameName(Connection connection, String requestDomainNameName) throws SQLException {
    DomainName domainName = DomainNameManager.getInstance().getByDomainNameName(requestDomainNameName);
    if (domainName == null) {
      return null;
    }
    return getById(connection, domainName.getSiteId());
  }

  public Site create(String name, String... domainNames) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      Site site = add(connection, name, domainNames);
      return site;
    }
  }

  public Site add(Connection connection, String name, String... domainNameNames) throws SQLException {
    connection.setAutoCommit(false);
    if (name == null || name.isEmpty()) {
      throw new InvalidParameterException("Invalid parameter name: " + name);
    }
    if (domainNameNames == null) {
      throw new InvalidParameterException("Invalid null parameter for domain names");
    }
    // TODO revisar que haya dominios que agregar

    String query = "INSERT INTO " + SitesTable.NAME + " (name) VALUES (?)";
    PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    ps.setString(1, name);
    Logger.fine(ps);
    ps.executeUpdate();

    ResultSet rs = ps.getGeneratedKeys();
    if (rs.next()) {
      int id = rs.getInt(1);

      DomainName baseDomainName = null;
      DomainNameList domainNames = new DomainNameList();
      for (int i = 0; i < domainNameNames.length; i++) {
        String domainNameName = domainNameNames[i];
        if (domainNameName == null || domainNameName.isEmpty()) {
          throw new InvalidParameterException("Invalid domain name: " + domainNameName);
        }
        DomainName domainName = DomainNameManager.getInstance().add(connection, id, domainNameName);
        if (baseDomainName == null) {
          baseDomainName = domainName;
        } else {
          domainNames.add(domainName);
        }
      }

      Site site = new Site(id, name, baseDomainName, domainNames, DEFAULT_VERSION);
      System.out.println("4: " + site);

      SiteManager.getInstance().update(connection, site);

      connection.setAutoCommit(true);
      return site;
    }
    throw new RuntimeException("Can't get the generated key");
  }

  public Site update(Connection connection, Site site) throws SQLException {
    String query = "UPDATE " + SitesTable.NAME + " SET name = ?, domainName = ? WHERE id = ?";
    PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    ps.setString(1, site.getName());
    ps.setInt(2, site.getBaseDomainName().getId());
    ps.setInt(3, site.getId());
    Logger.fine(ps);
    ps.executeUpdate();
    return site;
  }

  public SiteList list() throws SQLException {
    return list(null, null, null, null, null);
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

      String query
          = "SELECT s.id AS siteId, s.name AS name, s.domainName AS baseDomainNameId, d.id AS domainNameId, d.name AS baseDomainNameName, version "
          + "FROM " + SitesTable.NAME + " AS s "
          + "LEFT JOIN " + DomainNamesTable.NAME + " AS d ON s.domainName = d.id "
          + where + sqlSort + sqlLimit;

      PreparedStatement ps = connection.prepareStatement(query);
      setSiteFilters(filters, ps);

      Logger.fine(ps);
      ResultSet rs = ps.executeQuery();

      SiteList list = new SiteList(offset == null ? 0 : offset.getValue(), limit == null ? 0 : limit.getValue());
      Map<Integer, SiteHelper> map = new HashMap<>();

      while (rs.next()) {
        int id = rs.getInt("siteId");
        String name = rs.getString("name");
        int baseDomainNameId = rs.getInt("baseDomainNameId");
        int version = rs.getInt("version");

        SiteHelper siteHelper = map.get(id);
        if (siteHelper == null) {
          siteHelper = new SiteHelper(id, name, baseDomainNameId, version);
          map.put(id, siteHelper);
        }
        int domainNameId = rs.getInt("domainNameId");
        String domainNameName = rs.getString("baseDomainNameName");

        siteHelper.add(domainNameId, domainNameName);
      }
      for (Entry<Integer, SiteHelper> entry : map.entrySet()) {
        SiteHelper siteHelper = entry.getValue();
        Site site = siteHelper.getSite();
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

  void update(Site site, User owner) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  private static class SiteHelper {

    private final int id;
    private final String name;
    private final int baseDomainNameId;
    private DomainName baseDomainName;
    private final DomainNameList domainNameList = new DomainNameList();
    private final int version;

    private SiteHelper(int id, String name, int baseDomainNameId, int version) {
      this.id = id;
      this.name = name;
      this.baseDomainNameId = baseDomainNameId;
      this.version = version;
    }

    private void add(int domainNameId, String domainNameName) {
      DomainName domainName = new DomainName(domainNameId, id, domainNameName);
      if (domainNameId == baseDomainNameId) {
        baseDomainName = domainName;
      } else {
        domainNameList.add(domainName);
      }
    }

    private Site getSite() {
      Site site = new Site(id, name, baseDomainName, domainNameList, version);
      System.out.println("1: " + site);
      return site;
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

      String query = "SELECT count(*) AS total FROM " + DomainNamesTable.NAME + where + sqlSort + sqlLimit;
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

  DomainNameList listDomainName(Site site, Filters filters, Sort sort, Offset offset, Limit limit, User owner) throws SQLException {
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

      String query = "SELECT id, siteId, name FROM " + DomainNamesTable.NAME + where + sqlSort + sqlLimit;
      PreparedStatement ps = connection.prepareStatement(query);
      ps.setInt(1, site.getId());
      setHostFilters(filters, ps);

      Logger.fine(ps);
      ResultSet rs = ps.executeQuery();

      DomainNameList list = new DomainNameList(offset == null ? 0 : offset.getValue(), limit == null ? 0 : limit.getValue());
      while (rs.next()) {
        int id = rs.getInt("id");
        int siteId = rs.getInt("siteId");
        String name = rs.getString("name");

        DomainName domainName = new DomainName(id, siteId, name);

        list.add(domainName);
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
