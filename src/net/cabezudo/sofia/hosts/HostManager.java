package net.cabezudo.sofia.hosts;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
public class HostManager {

  private static HostManager INSTANCE;

  public static HostManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new HostManager();
    }
    return INSTANCE;
  }

  public HostList get(Site site) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return get(connection, site);
    }
  }

  public HostList get(Connection connection, Site site) throws SQLException {
    String query = "SELECT id, name FROM " + HostTable.NAME + " WHERE siteId = ?";

    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, site.getId());
    Logger.fine(ps);
    ResultSet rs = ps.executeQuery();

    HostList list = new HostList();

    while (rs.next()) {
      Integer id = rs.getInt(1);
      String name = rs.getString(2);
      Host host = new Host(id, name);
      list.add(host);
    }
    return list;
  }

  public Host add(Connection connection, int siteId, String domainName) throws SQLException {

    String query = "INSERT INTO " + HostTable.NAME + " (siteId, name) VALUES (?, ?)";
    PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    ps.setInt(1, siteId);
    ps.setString(2, domainName);
    Logger.fine(ps);
    ps.executeUpdate();
    connection.setAutoCommit(true);

    ResultSet rs = ps.getGeneratedKeys();
    if (rs.next()) {
      int id = rs.getInt(1);
      return new Host(id, domainName);
    }
    throw new RuntimeException("Can't get the generated key");
  }

  public Host get(int hostId) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return get(connection, hostId);
    }
  }

  public Host get(Connection connection, int hostId) throws SQLException {
    String query = "SELECT id, name FROM " + HostTable.NAME + " WHERE id = ?";

    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, hostId);
    Logger.fine(ps);
    ResultSet rs = ps.executeQuery();

    if (rs.next()) {
      Integer id = rs.getInt(1);
      String name = rs.getString(2);
      Host host = new Host(id, name);
      return host;
    }
    return null;
  }

  public void validate(String domainName) throws HostMaxSizeException, EmptyHostException, InvalidCharacterException, MissingDotException, HostNotExistsException {
    if (domainName.isEmpty()) {
      throw new EmptyHostException();
    }
    if (domainName.length() > Host.NAME_MAX_LENGTH) {
      throw new HostMaxSizeException(domainName.length());
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
      throw new HostNotExistsException();
    }
  }
}
