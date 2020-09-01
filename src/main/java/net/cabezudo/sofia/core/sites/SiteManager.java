package net.cabezudo.sofia.core.sites;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import net.cabezudo.sofia.core.InvalidParameterException;
import net.cabezudo.sofia.core.QueryHelper;
import net.cabezudo.sofia.core.api.options.OptionValue;
import net.cabezudo.sofia.core.api.options.list.Filters;
import net.cabezudo.sofia.core.api.options.list.Limit;
import net.cabezudo.sofia.core.api.options.list.Offset;
import net.cabezudo.sofia.core.api.options.list.Sort;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.sites.domainname.DomainName;
import net.cabezudo.sofia.core.sites.domainname.DomainNameList;
import net.cabezudo.sofia.core.sites.domainname.DomainNameManager;
import net.cabezudo.sofia.core.sites.domainname.DomainNamesTable;
import net.cabezudo.sofia.core.sites.validators.EmptySiteNameException;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UsersTable;
import net.cabezudo.sofia.emails.EMail;
import net.cabezudo.sofia.emails.EMailsTable;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.23
 */
public class SiteManager {

  public static final int DEFAULT_VERSION = 10000;
  private static SiteManager instance;

  public static SiteManager getInstance() {
    if (instance == null) {
      instance = new SiteManager();
    }
    return instance;
  }

  public Site getById(int id) throws SQLException {
    try ( Connection connection = Database.getConnection()) {
      return getById(connection, id);
    }
  }

  public Site getById(Connection connection, int siteId) throws SQLException {
    String query
            = "SELECT s.id AS siteId, s.name AS siteName, s.domainName AS baseDomainNameId, s.version AS siteVersion, d.id AS domainNameId, d.name AS domainNameName "
            + "FROM " + SitesTable.NAME + " AS s "
            + "LEFT JOIN " + DomainNamesTable.NAME + " AS d ON s.id = d.siteId "
            + "WHERE s.id = ? ORDER BY domainName";

    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setInt(1, siteId);
      Logger.fine("SiteManager", "getById", ps);
      rs = ps.executeQuery();
      if (!rs.next()) {
        return null;
      }
      return new Site(rs);
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
  }

  public Site getByHostame(String domainName) throws SQLException {
    try ( Connection connection = Database.getConnection()) {
      return getByHostame(connection, domainName);
    }
  }

  public Site getByHostame(Connection connection, String requestDomainNameName) throws SQLException {
    DomainName domainName = DomainNameManager.getInstance().getByDomainNameName(requestDomainNameName);
    if (domainName == null) {
      return null;
    }
    return getById(connection, domainName.getSiteId());
  }

  public Site create(String name, String... domainNames) throws SQLException, IOException {
    try ( Connection connection = Database.getConnection()) {
      return add(connection, name, domainNames);
    }
  }

  public Site add(Connection connection, String name, String... domainNameNames) throws SQLException, IOException {
    if (name == null || name.isEmpty()) {
      throw new InvalidParameterException("Invalid parameter name: " + name);
    }
    if (domainNameNames == null) {
      throw new InvalidParameterException("Invalid null parameter for domain names");
    }
    // TODO revisar que haya dominios que agregar

    String query = "INSERT INTO " + SitesTable.NAME + " (name) VALUES (?)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, name);
      Logger.fine(ps);
      ps.executeUpdate();

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        return createSite(connection, rs, name, domainNameNames);
      }
      throw new SofiaRuntimeException("Can't get the generated key");
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
  }

  private Site createSite(Connection connection, ResultSet rs, String name, String... domainNameNames) throws SQLException, IOException {
    int siteId = rs.getInt(1);
    DomainName baseDomainName = null;
    DomainNameList domainNames = new DomainNameList();
    for (String domainNameName : domainNameNames) {
      if (domainNameName == null || domainNameName.isEmpty()) {
        continue;
      }
      DomainName domainName = DomainNameManager.getInstance().add(connection, siteId, domainNameName);
      if (baseDomainName == null) {
        baseDomainName = domainName;
      }
      domainNames.add(domainName);
    }
    Site site = new Site(siteId, name, baseDomainName, domainNames, DEFAULT_VERSION);
    SiteManager.getInstance().update(connection, site);

    Path siteSourcesBasePath = site.getVersionedSourcesPath();
    if (!Files.exists(siteSourcesBasePath)) {
      Files.createDirectories(siteSourcesBasePath);
    }
    return site;
  }

  public Site update(Connection connection, Site site) throws SQLException {
    // TODO Update the domain name list
    String query = "UPDATE " + SitesTable.NAME + " SET name = ?, domainName = ? WHERE id = ?";
    try ( PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, site.getName());
      ps.setInt(2, site.getBaseDomainName().getId());
      ps.setInt(3, site.getId());
      Logger.fine(ps);
      ps.executeUpdate();
      return site;
    }
  }

  public SiteList list() throws SQLException {
    return list(null, null, null, null, null);
  }

  public SiteList list(Filters filters, Sort sort, Offset offset, Limit limit, User owner) throws SQLException {
    Logger.fine("Site list");

    try ( Connection connection = Database.getConnection()) {
      String where = getSiteWhere(filters);

      long sqlOffsetValue = 0;
      if (offset != null) {
        sqlOffsetValue = offset.getValue();
      }
      long sqlLimitValue = SiteList.MAX_PAGE_SIZE;
      if (limit != null) {
        sqlLimitValue = limit.getValue();
      }

      String sqlSort = QueryHelper.getOrderString(sort, "domainName", new String[]{"id", "name"});

      String sqlLimit = " LIMIT " + sqlOffsetValue + ", " + sqlLimitValue;

      String query
              = "SELECT s.id AS siteId, s.name AS siteName, s.domainName AS baseDomainNameId, s.version AS siteVersion, d.id AS domainNameId, d.name AS domainNameName "
              + "FROM " + SitesTable.NAME + " AS s "
              + "LEFT JOIN " + DomainNamesTable.NAME + " AS d ON s.id = d.siteId "
              + where + sqlSort + sqlLimit;

      PreparedStatement ps = null;
      ResultSet rs = null;
      SiteList list;
      try {
        ps = connection.prepareStatement(query);
        setSiteFilters(filters, ps);

        Logger.fine(ps);
        rs = ps.executeQuery();

        list = new SiteList(offset == null ? 0 : offset.getValue(), limit == null ? 0 : limit.getValue());

        while (rs.next()) {
          int id = rs.getInt("siteId");
          String name = rs.getString("siteName");
          int baseDomainNameId = rs.getInt("baseDomainNameId");
          int siteVersion = rs.getInt("siteVersion");
          int domainNameId = rs.getInt("domainNameId");
          String domainNameName = rs.getString("domainNameName");
          list.add(id, name, baseDomainNameId, siteVersion, domainNameId, domainNameName);
        }
        list.create();
      } finally {
        if (rs != null) {
          rs.close();
        }
        if (ps != null) {
          ps.close();
        }
      }
      try {
        query = "SELECT FOUND_ROWS() AS total";
        ps = connection.prepareStatement(query);
        Logger.fine(ps);
        rs = ps.executeQuery();
        if (!rs.next()) {
          throw new SofiaRuntimeException("The select to count the number of sites fail.");
        }
        int total = rs.getInt("total");

        list.setTotal(total);

        return list;
      } finally {
        if (rs != null) {
          rs.close();
        }
        if (ps != null) {
          ps.close();
        }
      }
    }
  }

  public void update(int siteId, String field, String value, User owner) throws SQLException, InvalidSiteVersionException, EmptySiteNameException {
    try ( Connection connection = Database.getConnection()) {
      update(connection, siteId, field, value, owner);
    }
  }

  public void update(Connection connection, int siteId, String field, String value, User owner) throws SQLException, InvalidSiteVersionException, EmptySiteNameException {
    switch (field) {
      case "name":
        validateName(value);
        break;
      case "version":
        validateVersion(value);
        break;
      default:
        throw new InvalidParameterException("Invalid parameter value: " + field);
    }
    String query = "UPDATE " + SitesTable.NAME + " SET " + field + " = ? WHERE id = ?";
    try ( PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, value);
      ps.setInt(2, siteId);
      Logger.fine(ps);
      ps.executeUpdate();
    }
  }

  public void validateName(String value) throws EmptySiteNameException {
    if (value == null || value.isEmpty()) {
      throw new EmptySiteNameException();
    }
    // TODO Max length
  }

  public void validateVersion(String value) throws InvalidSiteVersionException {
    int intValue;
    try {
      intValue = Integer.parseInt(value);
    } catch (NumberFormatException e) {
      throw new InvalidSiteVersionException("Invalid number", value);
    }
    if (intValue == 0) {
      throw new InvalidSiteVersionException("Invalid number", value);
    }
    // TODO Max length and size
  }

  public Site getByName(String name) throws SQLException {
    try ( Connection connection = Database.getConnection()) {
      return getByName(connection, name);
    }
  }

  public Site getByName(Connection connection, String name) throws SQLException {
    String query
            = "SELECT s.id AS id, s.name AS name, s.domainName AS baseDomainNameId, version AS siteVersion, d.id AS domainNameId, d.name AS domainNameName "
            + "FROM " + SitesTable.NAME + " AS s "
            + "LEFT JOIN " + DomainNamesTable.NAME + " AS d ON s.id = d.siteId "
            + "WHERE s.name = ? ORDER BY domainName";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setString(1, name);
      Logger.fine("SiteManager", "getByName", ps);
      rs = ps.executeQuery();

      if (!rs.next()) {
        return null;
      }

      return new Site(rs);
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
  }

  private synchronized void changeBasePath(Site site, DomainName domainName) throws IOException {
    Path oldSourceBasePath = site.getVersionedSourcesPath().getParent();
    Path newSourceBasePath = site.getVersionedSourcesPath(domainName).getParent();
    Logger.debug("Moving source path from %s to %s.", oldSourceBasePath, newSourceBasePath);
    Files.move(oldSourceBasePath, newSourceBasePath, ATOMIC_MOVE);
    Path oldBasePath = site.getBasePath();
    Path newBasePath = site.getBasePath(domainName);
    Logger.debug("Moving site path from %s to %s.", oldBasePath, newBasePath);
    try {
      if (Files.exists(oldBasePath, LinkOption.NOFOLLOW_LINKS)) {
        Files.move(oldBasePath, newBasePath, ATOMIC_MOVE);
      }
    } catch (IOException e) {
      Files.move(newSourceBasePath, oldSourceBasePath, ATOMIC_MOVE);
    }
  }

  public synchronized void update(Site site, DomainName domainName, User owner) throws SQLException {
    DomainName baseDomainName = site.getBaseDomainName();
    DomainNameManager.getInstance().update(domainName, owner);
    if (baseDomainName.getId() == domainName.getId()) {
      try {
        SiteManager.getInstance().changeBasePath(site, domainName);
      } catch (IOException e) {
        Logger.severe(e);
        DomainNameManager.getInstance().update(baseDomainName, owner);
      }
    }
  }

  public void delete(int siteId) throws SQLException {
    try ( Connection connection = Database.getConnection()) {
      delete(connection, siteId);
    }
  }

  public void delete(Connection connection, int siteId) throws SQLException {
    PreparedStatement dhps = null;
    PreparedStatement dsps = null;
    try {
      connection.setAutoCommit(false);
      String deleteHostsQuery = "DELETE FROM " + DomainNamesTable.NAME + " WHERE siteId = ?";
      dhps = connection.prepareStatement(deleteHostsQuery);
      dhps.setInt(1, siteId);
      Logger.fine(dhps);
      dhps.executeUpdate();
      String deleteSiteQuery = "DELETE FROM " + SitesTable.NAME + " WHERE id = ?";
      dsps = connection.prepareStatement(deleteSiteQuery);
      dsps.setInt(1, siteId);
      Logger.fine(dsps);
      dsps.executeUpdate();
      connection.commit();
    } catch (SQLException e) {
      connection.rollback();
      throw e;
    } finally {
      if (dsps != null) {
        dsps.close();
      }
      if (dhps != null) {
        dhps.close();
      }
    }
  }

  public Sites getByUserEMail(EMail eMail) throws SQLException {
    try ( Connection connection = Database.getConnection()) {
      return getByUserEMail(connection, eMail);
    }
  }

  public Sites getByUserEMail(Connection connection, EMail eMail) throws SQLException {
    String query
            = "SELECT s.id AS siteId, s.name AS siteName, s.domainName AS baseDomainNameId, s.version AS siteVersion, d.id AS domainNameId, h.name AS domainNameName "
            + "FROM " + SitesTable.NAME + " AS s "
            + "LEFT JOIN " + DomainNamesTable.NAME + " AS d ON s.id = d.siteId "
            + "LEFT JOIN " + DomainNamesTable.NAME + " AS d ON h.id = d.siteId "
            + "LEFT JOIN " + UsersTable.NAME + " AS u ON s.id = u.id "
            + "LEFT JOIN " + EMailsTable.NAME + " AS e ON u.eMail = e.id "
            + "WHERE address = ? "
            + "ORDER BY siteId";

    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setString(1, eMail.getAddress());
      Logger.fine("SiteManager", "getByUserEMail", ps);
      rs = ps.executeQuery();

      Sites sites = new Sites();

      while (rs.next()) {
        int id = rs.getInt("siteId");
        String name = rs.getString("name");
        int baseDomainNameId = rs.getInt("baseDomainNameId");
        int siteVersion = rs.getInt("siteVersion");
        int domainNameId = rs.getInt("domainNameId");
        String domainNameName = rs.getString("domainNameName");
        sites.add(id, name, baseDomainNameId, siteVersion, domainNameId, domainNameName);
      }
      sites.create();
      return sites;
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
  }

  public int getTotal(Filters filters, Sort sort, Offset offset, Limit limit, User owner) throws SQLException {
    Logger.fine("Site list total");

    try ( Connection connection = Database.getConnection()) {
      String where = getSiteWhere(filters);

      long sqlOffsetValue = 0;
      if (offset != null) {
        sqlOffsetValue = offset.getValue();
      }
      long sqlLimitValue = SiteList.MAX_PAGE_SIZE;
      if (limit != null) {
        sqlLimitValue = limit.getValue();
      }
      String sqlSort = QueryHelper.getOrderString(sort, "name", new String[]{"id", "name"});
      String sqlLimit = " LIMIT " + sqlOffsetValue + ", " + sqlLimitValue;

      String query = "SELECT count(*) AS total FROM " + SitesTable.NAME + where + sqlSort + sqlLimit;
      PreparedStatement ps = null;
      ResultSet rs = null;
      try {
        ps = connection.prepareStatement(query);
        setSiteFilters(filters, ps);

        Logger.fine(ps);
        rs = ps.executeQuery();

        if (rs.next()) {
          return rs.getInt("total");
        } else {
          throw new SofiaRuntimeException("Column expected.");
        }
      } finally {
        if (rs != null) {
          rs.close();
        }
        if (ps != null) {
          ps.close();
        }
      }
    }
  }

  private String getSiteWhere(Filters filter) {
    StringBuilder sb = new StringBuilder(" WHERE 1 = 1");
    if (filter != null) {
      List<OptionValue> values = filter.getValues();
      values.forEach(value -> {
        if (value.isPositive()) {
          sb.append(" AND (name LIKE ?)");
        } else {
          sb.append(" AND name NOT LIKE ?");
        }
      });
    }
    return sb.toString();
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

  public int getHostsTotal(Site site, Filters filters, Sort sort, Offset offset, Limit limit, User owner) throws SQLException {
    Logger.fine("Site host list total");

    PreparedStatement ps = null;
    ResultSet rs = null;
    try ( Connection connection = Database.getConnection()) {
      String where = getHostWhere(filters);

      long sqlOffsetValue = 0;
      if (offset != null) {
        sqlOffsetValue = offset.getValue();
      }
      long sqlLimitValue = SiteList.MAX_PAGE_SIZE;
      if (limit != null) {
        sqlLimitValue = limit.getValue();
      }

      String sqlSort = QueryHelper.getOrderString(sort, "name", new String[]{"id", "name"});

      String sqlLimit = " LIMIT " + sqlOffsetValue + ", " + sqlLimitValue;

      String query = "SELECT count(*) AS total FROM " + DomainNamesTable.NAME + where + sqlSort + sqlLimit;
      ps = connection.prepareStatement(query);

      int i = 1;
      ps.setInt(i, site.getId());

      setHostFilters(filters, ps);

      Logger.fine(ps);
      rs = ps.executeQuery();

      if (rs.next()) {
        int total = rs.getInt("total");
        return total;
      } else {
        throw new RuntimeException("Column expected.");
      }
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
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

  public DomainNameList listDomainName(Site site, Filters filters, Sort sort, Offset offset, Limit limit, User owner) throws SQLException {
    Logger.fine("Site list");

    try ( Connection connection = Database.getConnection()) {
      String where = getHostWhere(filters);

      long sqlOffsetValue = 0;
      if (offset != null) {
        sqlOffsetValue = offset.getValue();
      }
      long sqlLimitValue = SiteList.MAX_PAGE_SIZE;
      if (limit != null) {
        sqlLimitValue = limit.getValue();
      }

      String sqlSort = QueryHelper.getOrderString(sort, "name", new String[]{"id", "name"});

      String sqlLimit = " LIMIT " + sqlOffsetValue + ", " + sqlLimitValue;

      String query = "SELECT id, siteId, name FROM " + DomainNamesTable.NAME + where + sqlSort + sqlLimit;
      PreparedStatement ps = null;
      ResultSet rs = null;
      DomainNameList list;
      int i = 1;
      try {
        ps = connection.prepareStatement(query);
        ps.setInt(i, site.getId());
        setHostFilters(filters, ps);

        Logger.fine(ps);
        rs = ps.executeQuery();

        list = new DomainNameList(offset == null ? 0 : offset.getValue());
        while (rs.next()) {
          int id = rs.getInt("id");
          int siteId = rs.getInt("siteId");
          String name = rs.getString("name");

          DomainName domainName = new DomainName(id, siteId, name);
          list.add(domainName);
        }
      } finally {
        if (rs != null) {
          rs.close();
        }
        if (ps != null) {
          ps.close();
        }
      }

      query = "SELECT FOUND_ROWS() AS total";
      try {
        ps = connection.prepareStatement(query);
        Logger.fine(ps);
        rs = ps.executeQuery();
        if (!rs.next()) {
          throw new SofiaRuntimeException("The select to count the number of sites fail.");
        }
        int total = rs.getInt("total");

        list.setTotal(total);
      } finally {
        if (rs != null) {
          rs.close();
        }
        if (ps != null) {
          ps.close();
        }
      }

      return list;
    }
  }
}
