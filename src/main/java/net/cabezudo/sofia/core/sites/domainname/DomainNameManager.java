package net.cabezudo.sofia.core.sites.domainname;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.api.options.list.Filters;
import net.cabezudo.sofia.core.api.options.list.Limit;
import net.cabezudo.sofia.core.api.options.list.Offset;
import net.cabezudo.sofia.core.api.options.list.Sort;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.logger.Logger;

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
    try (Connection connection = Database.getConnection()) {
      return get(connection, site);
    }
  }

  public DomainNameList get(Connection connection, Site site) throws SQLException {
    String query = "SELECT id, name FROM " + DomainNamesTable.NAME + " WHERE siteId = ?";

    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setInt(1, site.getId());
      Logger.fine(ps);
      rs = ps.executeQuery();

      DomainNameList list = new DomainNameList(0);

      while (rs.next()) {
        int id = rs.getInt("id");
        int siteId = rs.getInt("siteId");
        String name = rs.getString("name");
        DomainName domainName = new DomainName(id, siteId, name);
        list.add(domainName);
      }
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

  public DomainName add(Connection connection, int siteId, String domainName) throws SQLException {

    String query = "INSERT INTO " + DomainNamesTable.NAME + " (siteId, name) VALUES (?, ?)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      ps.setInt(1, siteId);
      ps.setString(2, domainName);
      Logger.fine(ps);
      ps.executeUpdate();
      connection.setAutoCommit(true);

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new DomainName(id, siteId, domainName);
      }
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
    throw new SofiaRuntimeException("Can't get the generated key");
  }

  public DomainName get(int domainNameId) throws SQLException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, domainNameId);
    }
  }

  public DomainName get(Connection connection, int domainNameId) throws SQLException {
    String query = "SELECT id, siteId, name FROM " + DomainNamesTable.NAME + " WHERE id = ?";

    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setInt(1, domainNameId);
      Logger.fine(ps);
      rs = ps.executeQuery();

      if (rs.next()) {
        int id = rs.getInt("id");
        int siteId = rs.getInt("siteId");
        String name = rs.getString("name");
        return new DomainName(id, siteId, name);
      }
      return null;
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
  }

  public void validate(String domainName) throws DomainNameMaxSizeException, EmptyDomainNameException, InvalidCharacterException, MissingDotException, DomainNameNotExistsException {
    if (domainName.isEmpty()) {
      throw new EmptyDomainNameException();
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

  public DomainName getByDomainNameName(String domainNameName) throws SQLException {
    try (Connection connection = Database.getConnection()) {
      return getByDomainNameName(connection, domainNameName);
    }
  }

  public DomainName getByDomainNameName(Connection connection, String domainNameName) throws SQLException {
    // TODO agregar una cache aquí. No olvidar modificar los registros que se modifiquen.
    String query = "SELECT id, siteId, name FROM " + DomainNamesTable.NAME + " WHERE name = ?";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setString(1, domainNameName);
      Logger.fine(ps);
      rs = ps.executeQuery();

      if (rs.next()) {
        int id = rs.getInt("id");
        int siteId = rs.getInt("siteId");
        String name = rs.getString(2);
        return new DomainName(id, siteId, name);
      }
      return null;
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
  }

  public DomainName update(DomainName domainName, User owner) throws SQLException {
    try (Connection connection = Database.getConnection()) {
      return update(connection, domainName);
    }
  }

  public DomainName update(Connection connection, DomainName domainName) throws SQLException {
    String query = "UPDATE " + DomainNamesTable.NAME + " SET name = ? WHERE id = ?";
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, domainName.getName());
      ps.setInt(2, domainName.getId());
      Logger.fine(ps);
      ps.executeUpdate();
      return domainName;
    }
  }

  public void delete(int hostId) throws SQLException {
    try (Connection connection = Database.getConnection()) {
      delete(connection, hostId);
    }
  }

  public void delete(Connection connection, int hostId) throws SQLException {
    String query = "DELETE FROM " + DomainNamesTable.NAME + " WHERE id = ?";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setInt(1, hostId);
      Logger.fine(ps);
      ps.executeUpdate();
    }
  }
}
