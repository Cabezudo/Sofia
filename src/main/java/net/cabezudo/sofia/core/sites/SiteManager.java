package net.cabezudo.sofia.core.sites;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import net.cabezudo.sofia.core.InvalidParameterException;
import net.cabezudo.sofia.core.api.options.OptionValue;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.database.sql.Database;
import net.cabezudo.sofia.core.database.sql.Manager;
import net.cabezudo.sofia.core.database.sql.QueryHelper;
import net.cabezudo.sofia.core.database.sql.ValidSortColumns;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.list.Filters;
import net.cabezudo.sofia.core.list.Limit;
import net.cabezudo.sofia.core.list.Offset;
import net.cabezudo.sofia.core.list.Sort;
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
public class SiteManager extends Manager {

  public static final int DEFAULT_VERSION = 10000;
  private final ValidSortColumns validSortColumns = new ValidSortColumns("id", "name");
  private static SiteManager instance;

  public static SiteManager getInstance() {
    if (instance == null) {
      instance = new SiteManager();
    }
    return instance;
  }

  public Site getById(int id) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return getById(connection, id);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Site getById(Connection connection, int siteId) throws ClusterException {
    ResultSet rs = null;
    String query
            = "SELECT s.id AS siteId, s.name AS siteName, s.basePath AS sitebasePath, s.domainName AS baseDomainNameId, s.version AS siteVersion, d.id AS domainNameId, d.name AS domainNameName "
            + "FROM " + SitesTable.NAME + " AS s "
            + "LEFT JOIN " + DomainNamesTable.NAME + " AS d ON s.id = d.siteId "
            + "WHERE s.id = ? ORDER BY domainName";
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, siteId);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (!rs.next()) {
        return null;
      }
      return new Site(rs);
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public Site getByHostame(String domainName) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return getByHostame(connection, domainName);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Site getByHostame(Connection connection, String requestDomainNameName) throws ClusterException {
    DomainName domainName = DomainNameManager.getInstance().getByDomainNameName(requestDomainNameName);
    if (domainName == null) {
      return null;
    }
    return getById(connection, domainName.getSiteId());
  }

  public Site create(String name, Path basePath, String... domainNames) throws IOException, ClusterException {
    try (Connection connection = Database.getConnection()) {
      return add(connection, name, basePath, domainNames);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Site add(Connection connection, String name, Path basePath, String... domainNameNames) throws IOException, ClusterException {
    Logger.info("Create site using name '%s' and base path: %s", name, basePath);
    if (name == null || name.isEmpty()) {
      throw new InvalidParameterException("Invalid parameter name: " + name);
    }
    if (domainNameNames == null) {
      throw new InvalidParameterException("Invalid null parameter for domain names");
    }
    // TODO revisar que haya dominios que agregar

    ResultSet rs = null;
    String query = "INSERT INTO " + SitesTable.DATABASE_NAME + "." + SitesTable.NAME + " (name, basePath) VALUES (?, ?)";
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ps.setString(1, name);
      ps.setString(2, basePath.toString());
      ClusterManager.getInstance().executeUpdate(ps);
      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        return createSite(connection, rs, name, basePath, domainNameNames);
      }
      throw new SofiaRuntimeException("Can't get the generated key");
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  private Site createSite(Connection connection, ResultSet rs, String name, Path basePath, String... domainNameNames) throws IOException, ClusterException, SQLException {
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
    Site site = new Site(siteId, name, basePath, baseDomainName, domainNames, DEFAULT_VERSION);
    SiteManager.getInstance().update(connection, site);

    Path siteSourcesBasePath = site.getVersionedSourcesPath();
    if (!Files.exists(siteSourcesBasePath)) {
      Files.createDirectories(siteSourcesBasePath);
    }
    return site;
  }

  public Site update(Connection connection, Site site) throws ClusterException {
    // TODO Update the domain name list
    String query = "UPDATE " + SitesTable.NAME + " SET name = ?, domainName = ? WHERE id = ?";
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, site.getName());
      ps.setInt(2, site.getBaseDomainName().getId());
      ps.setInt(3, site.getId());
      ClusterManager.getInstance().executeUpdate(ps);
      return site;
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public SiteList list() throws ClusterException {
    return list(null, null, null, null, null);
  }

  public SiteList list(Filters filters, Sort sort, Offset offset, Limit limit, User owner) throws ClusterException {
    Logger.fine("Site list");

    try (Connection connection = Database.getConnection()) {
      String where = QueryHelper.getWhere(filters);

      long sqlOffsetValue = 0;
      if (offset != null) {
        sqlOffsetValue = offset.getValue();
      }
      long sqlLimitValue = SiteList.MAX_PAGE_SIZE;
      if (limit != null) {
        sqlLimitValue = limit.getValue();
      }

      String sqlSort = QueryHelper.getOrderString(sort, "domainName", validSortColumns);

      String sqlLimit = " LIMIT " + sqlOffsetValue + ", " + sqlLimitValue;
      String query
              = "SELECT s.id AS siteId, s.name AS siteName, s.basePath AS siteBasePath, s.domainName AS baseDomainNameId, s.version AS siteVersion, d.id AS domainNameId, d.name AS domainNameName "
              + "FROM " + SitesTable.NAME + " AS s "
              + "LEFT JOIN " + DomainNamesTable.NAME + " AS d ON s.id = d.siteId "
              + where + sqlSort + sqlLimit;
      ResultSet rs = null;
      SiteList list;
      try (PreparedStatement ps = connection.prepareStatement(query);) {
        QueryHelper.setFiltersValues(filters, ps);
        rs = ClusterManager.getInstance().executeQuery(ps);
        list = new SiteList(offset == null ? 0 : offset.getValue(), limit == null ? 0 : limit.getValue());
        while (rs.next()) {
          int id = rs.getInt("siteId");
          String name = rs.getString("siteName");
          Path basePath = Paths.get(rs.getString("siteBasePath"));
          int baseDomainNameId = rs.getInt("baseDomainNameId");
          int siteVersion = rs.getInt("siteVersion");
          int domainNameId = rs.getInt("domainNameId");
          String domainNameName = rs.getString("domainNameName");
          list.add(id, name, basePath, baseDomainNameId, siteVersion, domainNameId, domainNameName);
        }
        list.create();
      } catch (SQLException e) {
        throw new ClusterException(e);
      } finally {
        ClusterManager.getInstance().close(rs);
      }

      query = "SELECT FOUND_ROWS() AS total";
      try (PreparedStatement ps = connection.prepareStatement(query);) {
        rs = ClusterManager.getInstance().executeQuery(ps);
        if (!rs.next()) {
          throw new SofiaRuntimeException("The select to count the number of sites fail.");
        }
        int total = rs.getInt("total");

        list.setTotal(total);

        return list;
      } catch (SQLException e) {
        throw new ClusterException(e);
      } finally {
        ClusterManager.getInstance().close(rs);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public void update(int siteId, String field, String value, User owner) throws InvalidSiteVersionException, EmptySiteNameException, ClusterException {
    try (Connection connection = Database.getConnection()) {
      update(connection, siteId, field, value, owner);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public void update(Connection connection, int siteId, String field, String value, User owner) throws InvalidSiteVersionException, EmptySiteNameException, ClusterException {
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
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, value);
      ps.setInt(2, siteId);

      ClusterManager.getInstance().executeUpdate(ps);
    } catch (SQLException e) {
      throw new ClusterException(e);
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

  public Site getByName(String name) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return getByName(connection, name);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Site getByName(Connection connection, String name) throws ClusterException {
    String query
            = "SELECT s.id AS id, s.name AS name, s.domainName AS baseDomainNameId, version AS siteVersion, d.id AS domainNameId, d.name AS domainNameName "
            + "FROM " + SitesTable.NAME + " AS s "
            + "LEFT JOIN " + DomainNamesTable.NAME + " AS d ON s.id = d.siteId "
            + "WHERE s.name = ? ORDER BY domainName";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, name);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (!rs.next()) {
        return null;
      }
      return new Site(rs);
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  private synchronized void changeBasePath(Site site, Path basePath) throws IOException {
    Path oldSourceBasePath = site.getVersionedSourcesPath().getParent();
    Path newSourceBasePath = site.getVersionedSourcesPath(basePath).getParent();
    Logger.debug("Moving source path from %s to %s.", oldSourceBasePath, newSourceBasePath);
    Files.move(oldSourceBasePath, newSourceBasePath, ATOMIC_MOVE);
    Path oldBasePath = site.getBasePath();
    Path newBasePath = site.getBasePath(basePath);
    Logger.debug("Moving site path from %s to %s.", oldBasePath, newBasePath);
    try {
      if (Files.exists(oldBasePath, LinkOption.NOFOLLOW_LINKS)) {
        Files.move(oldBasePath, newBasePath, ATOMIC_MOVE);
      }
    } catch (IOException e) {
      Files.move(newSourceBasePath, oldSourceBasePath, ATOMIC_MOVE);
    }
  }

  public synchronized void update(Site site, DomainName domainName, User owner) throws ClusterException {
    DomainName baseDomainName = site.getBaseDomainName();
    Path basePath = site.getBasePath();
    if (basePath.toString().equals(baseDomainName.getName())) {
      basePath = Paths.get(domainName.getName());
    }
    DomainNameManager.getInstance().update(domainName, owner);
    if (baseDomainName.getId() == domainName.getId()) {
      try {
        SiteManager.getInstance().changeBasePath(site, basePath);
      } catch (IOException e) {
        Logger.severe(e);
        DomainNameManager.getInstance().update(baseDomainName, owner);
      }
    }
  }

  public void delete(int siteId) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      delete(connection, siteId);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public void delete(Connection connection, int siteId) throws ClusterException {
    String deleteHostsQuery = "DELETE FROM " + DomainNamesTable.NAME + " WHERE siteId = ?";
    String deleteSiteQuery = "DELETE FROM " + SitesTable.NAME + " WHERE id = ?";
    try (PreparedStatement dhps = connection.prepareStatement(deleteHostsQuery); PreparedStatement dsps = connection.prepareStatement(deleteSiteQuery);) {
      connection.setAutoCommit(false);
      dhps.setInt(1, siteId);
      ClusterManager.getInstance().executeQuery(dhps);
      dsps.setInt(1, siteId);
      ClusterManager.getInstance().executeQuery(dsps);
      connection.commit();
    } catch (SQLException e) {
      try {
        connection.rollback();
      } catch (SQLException rbe) {
        throw new ClusterException(rbe);
      }
      throw new ClusterException(e);
    }
  }

  public Sites getByUserEMail(EMail eMail) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return getByUserEMail(connection, eMail);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Sites getByUserEMail(Connection connection, EMail eMail) throws ClusterException {
    String query
            = "SELECT s.id AS siteId, s.name AS siteName, s.domainName AS baseDomainNameId, s.version AS siteVersion, d.id AS domainNameId, h.name AS domainNameName "
            + "FROM " + SitesTable.NAME + " AS s "
            + "LEFT JOIN " + DomainNamesTable.NAME + " AS d ON s.id = d.siteId "
            + "LEFT JOIN " + DomainNamesTable.NAME + " AS d ON h.id = d.siteId "
            + "LEFT JOIN " + UsersTable.NAME + " AS u ON s.id = u.id "
            + "LEFT JOIN " + EMailsTable.NAME + " AS e ON u.eMail = e.id "
            + "WHERE address = ? "
            + "ORDER BY siteId";

    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, eMail.getAddress());
      rs = ClusterManager.getInstance().executeQuery(ps);
      Sites sites = new Sites();
      while (rs.next()) {
        int id = rs.getInt("siteId");
        String name = rs.getString("name");
        Path basePath = Paths.get(rs.getString("basePath"));
        int baseDomainNameId = rs.getInt("baseDomainNameId");
        int siteVersion = rs.getInt("siteVersion");
        int domainNameId = rs.getInt("domainNameId");
        String domainNameName = rs.getString("domainNameName");
        sites.add(id, name, basePath, baseDomainNameId, siteVersion, domainNameId, domainNameName);
      }
      sites.create();
      return sites;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public int getTotal(Filters filters, Sort sort, Offset offset, Limit limit, User owner) throws ClusterException {
    Logger.fine("Site list total");

    try (Connection connection = Database.getConnection()) {
      String where = QueryHelper.getWhere(filters);

      long sqlOffsetValue = 0;
      if (offset != null) {
        sqlOffsetValue = offset.getValue();
      }
      long sqlLimitValue = SiteList.MAX_PAGE_SIZE;
      if (limit != null) {
        sqlLimitValue = limit.getValue();
      }
      String sqlSort = QueryHelper.getOrderString(sort, "name", validSortColumns);
      String sqlLimit = " LIMIT " + sqlOffsetValue + ", " + sqlLimitValue;

      String query = "SELECT count(*) AS total FROM " + SitesTable.NAME + where + sqlSort + sqlLimit;
      ResultSet rs = null;
      try (PreparedStatement ps = connection.prepareStatement(query);) {
        QueryHelper.setFiltersValues(filters, ps);
        rs = ClusterManager.getInstance().executeQuery(ps);
        if (rs.next()) {
          return rs.getInt("total");
        } else {
          throw new SofiaRuntimeException("Column expected.");
        }
      } finally {
        ClusterManager.getInstance().close(rs);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public int getHostsTotal(Site site, Filters filters, Sort sort, Offset offset, Limit limit, User owner) throws ClusterException {
    Logger.fine("Site host list total");

    String where = getHostWhere(filters);

    long sqlOffsetValue = 0;
    if (offset != null) {
      sqlOffsetValue = offset.getValue();
    }
    long sqlLimitValue = SiteList.MAX_PAGE_SIZE;
    if (limit != null) {
      sqlLimitValue = limit.getValue();
    }

    String sqlSort = QueryHelper.getOrderString(sort, "name", validSortColumns);

    String sqlLimit = " LIMIT " + sqlOffsetValue + ", " + sqlLimitValue;

    String query = "SELECT count(*) AS total FROM " + DomainNamesTable.NAME + where + sqlSort + sqlLimit;
    ResultSet rs = null;
    try (Connection connection = Database.getConnection(); PreparedStatement ps = connection.prepareStatement(query);) {
      int i = 1;
      ps.setInt(i, site.getId());
      setHostFilters(filters, ps);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        return rs.getInt("total");
      } else {
        throw new SofiaRuntimeException("Column expected.");
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
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

  public DomainNameList listDomainName(Site site, Filters filters, Sort sort, Offset offset, Limit limit, User owner) throws ClusterException {
    Logger.fine("Site list");

    try (Connection connection = Database.getConnection()) {
      String where = getHostWhere(filters);

      long sqlOffsetValue = 0;
      if (offset != null) {
        sqlOffsetValue = offset.getValue();
      }
      long sqlLimitValue = SiteList.MAX_PAGE_SIZE;
      if (limit != null) {
        sqlLimitValue = limit.getValue();
      }

      String sqlSort = QueryHelper.getOrderString(sort, "name", validSortColumns);
      String sqlLimit = " LIMIT " + sqlOffsetValue + ", " + sqlLimitValue;

      ResultSet rs = null;
      DomainNameList list;
      int i = 1;
      String query = "SELECT id, siteId, name FROM " + DomainNamesTable.NAME + where + sqlSort + sqlLimit;
      try (PreparedStatement ps = connection.prepareStatement(query);) {
        ps.setInt(i, site.getId());
        setHostFilters(filters, ps);

        rs = ClusterManager.getInstance().executeQuery(ps);

        list = new DomainNameList(offset == null ? 0 : offset.getValue());
        while (rs.next()) {
          int id = rs.getInt("id");
          int siteId = rs.getInt("siteId");
          String name = rs.getString("name");

          DomainName domainName = new DomainName(id, siteId, name);
          list.add(domainName);
        }
      } finally {
        ClusterManager.getInstance().close(rs);
      }

      query = "SELECT FOUND_ROWS() AS total";
      try (PreparedStatement ps = connection.prepareStatement(query);) {
        rs = ClusterManager.getInstance().executeQuery(ps);
        if (!rs.next()) {
          throw new SofiaRuntimeException("The select to count the number of sites fail.");
        }
        int total = rs.getInt("total");
        list.setTotal(total);
      } finally {
        ClusterManager.getInstance().close(rs);
      }
      return list;
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }
}
