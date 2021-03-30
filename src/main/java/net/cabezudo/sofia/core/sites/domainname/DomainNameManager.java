package net.cabezudo.sofia.core.sites.domainname;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.list.Filters;
import net.cabezudo.sofia.core.list.Limit;
import net.cabezudo.sofia.core.list.Offset;
import net.cabezudo.sofia.core.list.Sort;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.database.sql.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.validation.EmptyValueException;
import net.cabezudo.sofia.core.validation.InvalidCharacterException;

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

  public DomainNameList get(Site site) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, site);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public DomainNameList get(Connection connection, Site site) throws ClusterException {
    String query = "SELECT id, name FROM " + DomainNamesTable.NAME + " WHERE siteId = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, site.getId());
      rs = ClusterManager.getInstance().executeQuery(ps);
      DomainNameList list = new DomainNameList(0);
      while (rs.next()) {
        int id = rs.getInt("id");
        int siteId = rs.getInt("siteId");
        String name = rs.getString("name");
        DomainName domainName = new DomainName(id, siteId, name);
        list.add(domainName);
      }
      return list;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public DomainName add(Connection connection, int siteId, String domainNameName) throws ClusterException {
    String query = "INSERT INTO " + DomainNamesTable.NAME + " (siteId, name) VALUES (?, ?)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ps.setInt(1, siteId);
      ps.setString(2, domainNameName);
      ClusterManager.getInstance().executeUpdate(ps);
      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new DomainName(id, siteId, domainNameName);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    throw new SofiaRuntimeException("Can't get the generated key");
  }

  public DomainName get(int domainNameId) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, domainNameId);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public DomainName get(Connection connection, int domainNameId) throws ClusterException {
    String query = "SELECT id, siteId, name FROM " + DomainNamesTable.NAME + " WHERE id = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, domainNameId);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        int id = rs.getInt("id");
        int siteId = rs.getInt("siteId");
        String name = rs.getString("name");
        return new DomainName(id, siteId, name);
      }
      return null;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public void validate(String domainName) throws DomainNameMaxSizeException, EmptyValueException, InvalidCharacterException, MissingDotException, DomainNameNotExistsException {
    if (domainName.isEmpty()) {
      throw new EmptyValueException();
    }
    if (domainName.length() > DomainName.NAME_MAX_LENGTH) {
      throw new DomainNameMaxSizeException(domainName.length());
    }

    int dotCounter = 0;
    for (int i = 0; i < domainName.length(); i++) {
      Character c = domainName.charAt(i);
      if (!Character.isLetterOrDigit(c) && c != '.' && c != '-' && c != '_') {
        throw new InvalidCharacterException(c);
      }
      if (c == '.') {
        dotCounter++;
      }
    }
    if (dotCounter == 0) {
      throw new MissingDotException();
    }

    try {
      InetAddress.getByName(domainName);
    } catch (UnknownHostException e) {
      throw new DomainNameNotExistsException();
    }
  }

  public DomainNameList listDomainNames(Site site, Filters filters, Sort sort, Offset offset, Limit limit, User owner) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  public DomainName getByDomainNameName(String domainNameName) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return getByDomainNameName(connection, domainNameName);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public DomainName getByDomainNameName(Connection connection, String domainNameName) throws ClusterException {
    // TODO agregar una cache aquí. No olvidar modificar los registros que se modifiquen.
    String query = "SELECT id, siteId, name FROM " + DomainNamesTable.NAME + " WHERE name = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, domainNameName);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        int id = rs.getInt("id");
        int siteId = rs.getInt("siteId");
        String name = rs.getString(2);
        return new DomainName(id, siteId, name);
      }
      return null;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public DomainName update(DomainName domainName, User owner) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return update(connection, domainName);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public DomainName update(Connection connection, DomainName domainName) throws ClusterException {
    String query = "UPDATE " + DomainNamesTable.NAME + " SET name = ? WHERE id = ?";
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, domainName.getName());
      ps.setInt(2, domainName.getId());

      ClusterManager.getInstance().executeUpdate(ps);
      return domainName;
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public void delete(int hostId) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      delete(connection, hostId);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public void delete(Connection connection, int hostId) throws ClusterException {
    String query = "DELETE FROM " + DomainNamesTable.NAME + " WHERE id = ?";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setInt(1, hostId);
      ClusterManager.getInstance().executeUpdate(ps);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }
}
