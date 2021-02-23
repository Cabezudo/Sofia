package net.cabezudo.sofia.core.server.html;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.cache.Cache;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.http.domains.DomainName;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.01
 */
public class URLManager {

  private static final URLManager INSTANCE = new URLManager();

  private URLManager() {
    // Nothing to do here
  }

  public static URLManager getInstance() {
    return INSTANCE;
  }

  void changeCompanyHost(Site site, SofiaHTMLServletRequest request) throws ClusterException {
    Logger.debug("request on changeCompanyHost: %s", request);
    String serverName = request.getServerName();
    String requestURI = request.getRequestURI();
    Logger.debug("requestURI: %s", requestURI);

    if (requestURI.startsWith("/api") || requestURI.startsWith("/images")) {
      return;
    }

    String subdomain = null;
    Cache<String, RequestData> requestDataCache = RequestData.getCache();
    RequestData requestData = requestDataCache.get(serverName);

    if (requestData == null) {
      Logger.debug(serverName + " NOT FOUND in cache");
      subdomain = getCompanyPathByServerName(site, serverName);
      if (subdomain == null) {
        Logger.debug("Subdomain NOT FOUND in database for server name %s", serverName);
      } else {
        Logger.debug("Subdomain %s FOUND in database for server name %s", subdomain, serverName);
        requestURI = "/" + subdomain + requestURI;
        DomainName domainName = new DomainName(serverName);
        serverName = domainName.parent().toString();
        requestData = new RequestData(serverName, subdomain);
        requestDataCache.put(serverName, requestData);
        request.setRequestURI(requestURI);
        request.setServerName(serverName);
      }
    }
  }

  void changeCompanyPath(Site site, SofiaHTMLServletRequest request) throws ClusterException {
    Logger.debug("request on changeCompanyPath: %s", request);
    String requestURI = request.getRequestURI();

    String path = requestURI.substring(1);
    int i = path.indexOf("/");
    String companyPath;
    if (i == -1) {
      companyPath = path;
    } else {
      companyPath = path.substring(0, i);
    }
    if (exists(site, companyPath)) {
      Logger.debug("Found company for %s", companyPath);
      if (!requestURI.endsWith("variables.js")) {
        requestURI = "/company" + path.substring(companyPath.length());
        request.setRequestURI(requestURI);
        Logger.debug("The file is not variables.js");
      }
      Logger.debug("Company directory FOUND in path. Add file. Request URI: %s", request.getRequestURI());
    }
  }

  void changeDomainName(SofiaHTMLServletRequest request) {
    String serverName = request.getServerName();

    // TODO set this in database.
    if ("cdmx.menu".equals(serverName)) {
      request.setServerName("hayquecomer.com");
    }
  }

  public int add(Connection connection, Site site, String serverName, String companyPath) throws ClusterException {
    String query = "INSERT INTO " + URLTable.NAME + " (siteId, serverName, companyPath) VALUES (?, ?, ?)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ps.setInt(1, site.getId());
      ps.setString(2, serverName);
      ps.setString(3, companyPath);
      ClusterManager.getInstance().executeUpdate(ps);
      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        return rs.getInt(1);
      }
      throw new SofiaRuntimeException("Can't get the generated key");
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  private String getCompanyPathByServerName(Site site, String serverName) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return getCompanyPathByServerName(connection, site, serverName);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public String getCompanyPathByServerName(Connection connection, Site site, String serverName) throws ClusterException {
    String query = "SELECT id, siteId, serverName, companyPath FROM " + URLTable.NAME + " WHERE siteId = ? AND serverName = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, site.getId());
      ps.setString(2, serverName);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (!rs.next()) {
        return null;
      }
      return rs.getString("companyPath");
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public boolean exists(Site site, String companyPath) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return URLManager.this.exists(connection, site, companyPath);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public boolean exists(Connection connection, Site site, String companyPath) throws ClusterException {
    String query = "SELECT id, siteId, serverName, companyPath FROM " + URLTable.NAME + " WHERE siteId = ? AND companyPath = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, site.getId());
      ps.setString(2, companyPath);
      rs = ClusterManager.getInstance().executeQuery(ps);
      return rs.next();
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }
}
