package net.cabezudo.sofia.core.users;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import net.cabezudo.sofia.core.api.options.list.Filters;
import net.cabezudo.sofia.core.api.options.list.Limit;
import net.cabezudo.sofia.core.api.options.list.Offset;
import net.cabezudo.sofia.core.api.options.list.Sort;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.configuration.ConfigurationException;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.database.Manager;
import net.cabezudo.sofia.core.database.QueryHelper;
import net.cabezudo.sofia.core.database.ValidSortColumns;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.mail.MailServerException;
import net.cabezudo.sofia.core.mail.Message;
import net.cabezudo.sofia.core.passwords.Hash;
import net.cabezudo.sofia.core.passwords.Password;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SitesTable;
import net.cabezudo.sofia.core.templates.EMailTemplate;
import net.cabezudo.sofia.core.templates.TemplatesManager;
import net.cabezudo.sofia.core.users.profiles.Profile;
import net.cabezudo.sofia.core.users.profiles.Profiles;
import net.cabezudo.sofia.core.users.profiles.UsersProfilesTable;
import net.cabezudo.sofia.customers.CustomerService;
import net.cabezudo.sofia.emails.EMail;
import net.cabezudo.sofia.emails.EMailAddressNotExistException;
import net.cabezudo.sofia.emails.EMailManager;
import net.cabezudo.sofia.emails.EMailNotExistException;
import net.cabezudo.sofia.emails.EMailsTable;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.people.PeopleManager;
import net.cabezudo.sofia.people.PeopleTable;
import net.cabezudo.sofia.people.Person;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.16
 */
public class UserManager extends Manager {

  private static final UserManager INSTANCE = new UserManager();
  private final ValidSortColumns validSortColumns = new ValidSortColumns("id", "name");

  private UserManager() {
    // Just to protect the instance
  }

  public static UserManager getInstance() {
    return INSTANCE;
  }

  public User getAdministrator() throws ClusterException {
    return this.get(1);
  }

  public Person getPerson(Connection connection, EMail eMail) throws ClusterException {
    String query = "SELECT id, name, lastName, owner FROM " + PeopleTable.NAME + " WHERE primaryEMailId=? AND owner=1";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, eMail.getId());
      rs = ClusterManager.getInstance().executeQuery(ps);

      if (rs.next()) {
        int id = rs.getInt(1);
        String name = rs.getString(1);
        String lastName = rs.getString(1);
        return new Person(id, name, lastName, 1);
      } else {
        throw new SofiaRuntimeException("Key not generated.");
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public Person add(Connection connection, String name, String lastName, int ownerId) throws ClusterException {
    String query = "INSERT INTO " + PeopleTable.NAME + " (firstName, lastName, owner) VALUES (?, ?, ?)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ps.setString(1, name);
      ps.setString(2, lastName);
      ps.setInt(3, ownerId);
      ClusterManager.getInstance().executeUpdate(ps);
      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new Person(id, name, lastName, 1);
      } else {
        throw new SofiaRuntimeException("Key not generated.");
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public Person updatePerson(Connection connection, int id, String name, String lastName, int ownerId) throws ClusterException {
    String query = "UPDATE " + PeopleTable.NAME + " SET name=?, lastName=?, owner=? WHERE id=?";
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, name);
      ps.setString(2, lastName);
      ps.setInt(3, ownerId);

      ClusterManager.getInstance().executeUpdate(ps);
      return new Person(id, name, lastName, ownerId);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public User login(Site site, String emailAddress, Password password) throws ClusterException {
    ResultSet rs = null;
    String query
            = "SELECT u.id, site, e.id AS eMailId, p.id AS personId, e.address AS address, creationDate, activated, passwordRecoveryUUID, passwordRecoveryDate "
            + "FROM " + UsersTable.NAME + " AS u "
            + "LEFT JOIN " + EMailsTable.NAME + " AS e ON u.eMail = e.id "
            + "LEFT JOIN " + PeopleTable.NAME + " AS p ON e.personId = p.id "
            + "WHERE address = ? AND (site = ? OR u.id = 1) AND password = ?";
    try (Connection connection = Database.getConnection(); PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, emailAddress);
      ps.setInt(2, site.getId());
      ps.setBytes(3, password.getBytes());

      rs = ClusterManager.getInstance().executeQuery(ps);

      if (rs.next()) {
        int id = rs.getInt("id");
        int siteId = rs.getInt("site");
        int eMailId = rs.getInt("eMailId");
        int personId = rs.getInt("personId");
        String address = rs.getString("address");
        EMail eMail = new EMail(eMailId, personId, address);
        Date creationDate = rs.getDate("creationDate");
        boolean activated = rs.getBoolean("activated");
        String passwordRecoveryUUID = rs.getString("passwordRecoveryUUID");
        Date passwordRecoveryDate = rs.getDate("passwordRecoveryDate");

        return new User(id, siteId, eMail, creationDate, activated, passwordRecoveryUUID, passwordRecoveryDate);
      }
      return null;
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Message getRecoveryEMailData(Site site, String address, Hash hash) throws IOException, ClusterException, ConfigurationException {
    try (Connection connection = Database.getConnection()) {
      updateHash(connection, address, hash);
      Person person = PeopleManager.getInstance().getByEMailAddress(connection, address);
      EMailTemplate emailRecoveryTemplate = TemplatesManager.getInstance().getEMailPasswordRecoveryTemplate(person.getLocale());

      emailRecoveryTemplate.set("name", person.getName());
      emailRecoveryTemplate.set("site.name", site.getName());
      emailRecoveryTemplate.set("password.change.uri", site.getPasswordChangeURI() + "?" + hash);
      // TODO Set the hash time on the site configuration
      emailRecoveryTemplate.set("password.change.hash.time", "120");
      emailRecoveryTemplate.set("site.uri", site.getBaseDomainName().toString());

      EMail from = site.getNoReplyEMail();
      EMail to = EMailManager.getInstance().get(connection, address);

      return new Message(
              site.getNoReplyName(),
              from,
              person.getName() + ' ' + person.getLastName(),
              to,
              emailRecoveryTemplate.getSubject(),
              emailRecoveryTemplate.getPlainText(),
              emailRecoveryTemplate.getHtmlText());
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  private void updateHash(Connection connection, String address, Hash hash) throws ClusterException {
    String query
            = "UPDATE " + UsersTable.NAME + " "
            + "SET passwordRecoveryUUID = ?, passwordRecoveryDate = ? "
            + "WHERE eMail = (SELECT id FROM " + EMailsTable.NAME + " WHERE address = ?)";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setString(1, hash.toString());
      Timestamp timestamp = new Timestamp(new Date().getTime());
      ps.setTimestamp(2, timestamp);
      ps.setString(3, address);

      ClusterManager.getInstance().executeUpdate(ps);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Message getPasswordChangedEMailData(Site site, String address) throws IOException, ClusterException {
    try (Connection connection = Database.getConnection()) {
      Person person = PeopleManager.getInstance().getByEMailAddress(connection, address);

      EMailTemplate emailRecoveryTemplate = TemplatesManager.getInstance().getEMailPasswordChangedTemplate(person.getLocale());

      emailRecoveryTemplate.set("name", person.getName());
      emailRecoveryTemplate.set("site.name", site.getName());
      emailRecoveryTemplate.set("site.uri", site.getURL().toString());

      EMail from = site.getNoReplyEMail();
      EMail to = EMailManager.getInstance().get(connection, address);

      return new Message(
              site.getNoReplyName(),
              from,
              person.getName() + ' ' + person.getLastName(),
              to,
              emailRecoveryTemplate.getSubject(),
              emailRecoveryTemplate.getPlainText(),
              emailRecoveryTemplate.getHtmlText());
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public void set(Site site, String address, Password password) throws EMailAddressNotExistException, ClusterException {
    try (Connection connection = Database.getConnection()) {
      set(connection, site, address, password);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public User set(Connection connection, Site site, String address, Password password) throws EMailAddressNotExistException, ClusterException {
    ResultSet rs = null;
    String query = "INSERT INTO " + UsersTable.NAME + " (site, eMail, password, activated) VALUES (?, ?, ?, TRUE)";
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      EMail eMail = EMailManager.getInstance().get(connection, address);
      if (eMail == null) {
        throw new EMailAddressNotExistException("Can't find the e-mail with address " + address + ".", address);
      }
      deactivateAllPasswords(connection, eMail);
      ps.setInt(1, site.getId());
      ps.setInt(2, eMail.getId());
      ps.setBytes(3, password.getBytes());
      ClusterManager.getInstance().executeUpdate(ps);
      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int userId = rs.getInt(1);
        boolean activated = true;
        return new User(userId, site.getId(), eMail, null, activated, null, null);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    throw new ClusterException("Can't get the generated key");
  }

  private void deactivateAllPasswords(Connection connection, EMail eMail) throws ClusterException {
    String query = "UPDATE " + UsersTable.NAME + " SET activated = false WHERE eMail = ?";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setLong(1, eMail.getId());
      ClusterManager.getInstance().executeUpdate(ps);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }

  }

  public User getByEMail(String address, Site site) throws ClusterException {
    ResultSet rs = null;
    String query
            = "SELECT u.id, site, e.id AS eMailId, p.id AS personId, e.address AS address, creationDate, activated, activated, passwordRecoveryUUID, passwordRecoveryDate "
            + "FROM " + UsersTable.NAME + " AS u "
            + "LEFT JOIN " + EMailsTable.NAME + " AS e ON u.eMail = e.id "
            + "LEFT JOIN " + PeopleTable.NAME + " AS p ON e.personId = p.id "
            + "WHERE address = ? AND site = ?";
    try (Connection connection = Database.getConnection(); PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, address);
      ps.setInt(2, site.getId());
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        int id = rs.getInt("id");
        int siteId = rs.getInt("site");
        int eMailId = rs.getInt("eMailId");
        int personId = rs.getInt("personId");
        EMail eMail = new EMail(eMailId, personId, address);
        Date creationDate = rs.getDate("creationDate");
        boolean activated = rs.getBoolean("activated");
        String passwordRecoveryUUID = rs.getString("passwordRecoveryUUID");
        Date passwordRecoveryDate = rs.getDate("passwordRecoveryDate");
        return new User(id, siteId, eMail, creationDate, activated, passwordRecoveryUUID, passwordRecoveryDate);
      }
      return null;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public Users getByEMail(String address) throws ClusterException {
    ResultSet rs = null;
    String query
            = "SELECT u.id, site, e.id AS eMailId, p.id AS personId, e.address AS address, creationDate, activated, activated, passwordRecoveryUUID, passwordRecoveryDate "
            + "FROM " + UsersTable.NAME + " AS u "
            + "LEFT JOIN " + EMailsTable.NAME + " AS e ON u.eMail = e.id "
            + "LEFT JOIN " + PeopleTable.NAME + " AS p ON e.personId = p.id "
            + "WHERE address = ?";
    try (Connection connection = Database.getConnection(); PreparedStatement ps = connection.prepareStatement(query);) {
      Users users = new Users();
      ps.setString(1, address);
      rs = ClusterManager.getInstance().executeQuery(ps);
      while (rs.next()) {
        int id = rs.getInt("id");
        int siteId = rs.getInt("site");
        int eMailId = rs.getInt("eMailId");
        int personId = rs.getInt("personId");
        EMail eMail = new EMail(eMailId, personId, address);
        Date creationDate = rs.getDate("creationDate");
        boolean activated = rs.getBoolean("activated");
        String passwordRecoveryUUID = rs.getString("passwordRecoveryUUID");
        Date passwordRecoveryDate = rs.getDate("passwordRecoveryDate");

        User user = new User(id, siteId, eMail, creationDate, activated, passwordRecoveryUUID, passwordRecoveryDate);
        users.add(user);
      }
      return null;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public User getByPersonId(int personId) throws SQLException, ClusterException {
    try (Connection connection = Database.getConnection()) {
      return getByPersonId(connection, personId);
    }
  }

  public User getByPersonId(Connection connection, int personId) throws ClusterException {
    String query
            = "SELECT u.id, site, e.id AS eMailId, p.id AS personId, e.address AS address, creationDate, activated, activated, passwordRecoveryUUID, passwordRecoveryDate "
            + "FROM " + UsersTable.NAME + " AS u "
            + "LEFT JOIN " + EMailsTable.NAME + " AS e ON u.eMail = e.id "
            + "LEFT JOIN " + PeopleTable.NAME + " AS p ON e.personId = p.id "
            + "WHERE personId = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, personId);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        int id = rs.getInt("id");
        int siteId = rs.getInt("site");
        int eMailId = rs.getInt("eMailId");
        String address = rs.getString("address");
        EMail eMail = new EMail(eMailId, personId, address);
        Date creationDate = rs.getDate("creationDate");
        boolean activated = rs.getBoolean("activated");
        String passwordRecoveryUUID = rs.getString("passwordRecoveryUUID");
        Date passwordRecoveryDate = rs.getDate("passwordRecoveryDate");

        return new User(id, siteId, eMail, creationDate, activated, passwordRecoveryUUID, passwordRecoveryDate);
      }
      return null;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public User get(int id) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, id);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public User get(Connection connection, int id) throws ClusterException {
    String query
            = "SELECT u.id, site, e.id AS eMailId, p.id AS personId, e.address AS address, creationDate, activated, activated, passwordRecoveryUUID, passwordRecoveryDate "
            + "FROM " + UsersTable.NAME + " AS u "
            + "LEFT JOIN " + EMailsTable.NAME + " AS e ON u.eMail = e.id "
            + "LEFT JOIN " + PeopleTable.NAME + " AS p ON e.personId = p.id "
            + "WHERE u.id = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, id);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        int siteId = rs.getInt("siteId");
        String siteName = rs.getString("siteName");
        int eMailId = rs.getInt("eMailId");
        int personId = rs.getInt("personId");
        String address = rs.getString("address");
        EMail eMail = new EMail(eMailId, personId, address);
        Date creationDate = rs.getDate("creationDate");
        boolean activated = rs.getBoolean("activated");
        String passwordRecoveryUUID = rs.getString("passwordRecoveryUUID");
        Date passwordRecoveryDate = rs.getDate("passwordRecoveryDate");
        return new User(id, siteId, eMail, creationDate, activated, passwordRecoveryUUID, passwordRecoveryDate);
      }
      return null;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public User getByHash(Connection connection, Hash hash) throws ClusterException {
    String query
            = "SELECT u.id, site, e.id AS eMailId, p.id AS personId, e.address AS address, creationDate, activated, activated, passwordRecoveryUUID, passwordRecoveryDate "
            + "FROM " + UsersTable.NAME + " AS u "
            + "LEFT JOIN " + EMailsTable.NAME + " AS e ON u.eMail = e.id "
            + "LEFT JOIN " + PeopleTable.NAME + " AS p ON e.personId = p.id "
            + "WHERE passwordRecoveryUUID = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, hash.toString());
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        int id = rs.getInt("id");
        int siteId = rs.getInt("site");
        int eMailId = rs.getInt("eMailId");
        int personId = rs.getInt("personId");
        String address = rs.getString("address");
        EMail eMail = new EMail(eMailId, personId, address);
        Date creationDate = rs.getDate("creationDate");
        boolean activated = rs.getBoolean("activated");
        String passwordRecoveryUUID = rs.getString("passwordRecoveryUUID");
        Date passwordRecoveryDate = rs.getDate("passwordRecoveryDate");
        return new User(id, siteId, eMail, creationDate, activated, passwordRecoveryUUID, passwordRecoveryDate);
      }
      return null;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public void changePassword(Site site, Hash hash, Password password) throws MailServerException, IOException, EMailNotExistException, UserNotFoundByHashException, NullHashException, HashTooOldException, ClusterException {
    try (Connection connection = Database.getConnection()) {
      User user = UserManager.getInstance().getByHash(connection, hash);
      if (user == null) {
        throw new UserNotFoundByHashException("change.password.user.not.found.by.hash");
      }
      if (user.getPasswordRecoveryHash() == null) {
        throw new NullHashException("change.password.hash.null");
      }
      long now = new Date().getTime() / 1000;
      long mailMaxAge = site.getPasswordChangeHashExpireTime();
      long hashAge = user.getPasswordRecoveryDate().getTime();
      long mailAge = now - hashAge;
      if (mailAge > mailMaxAge) {
        throw new HashTooOldException("change.password.hash.old");
      }

      String query = "UPDATE " + UsersTable.NAME + " SET passwordRecoveryUUID = ?, password = ? WHERE site = ? AND passwordRecoveryUUID = ?";
      try (PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setString(1, hash.toString());
        ps.setBytes(2, password.getBytes());
        ps.setInt(3, site.getId());
        ps.setString(4, hash.toString());

        ClusterManager.getInstance().executeUpdate(ps);

        EMail email = EMailManager.getInstance().get(connection, user.getMail().getId());
        CustomerService.sendPasswordChangedEMail(site, email.getAddress());
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public void changePassword(User user, Password password) throws MailServerException, IOException, EMailNotExistException, UserNotFoundByHashException, NullHashException, HashTooOldException, ClusterException {
    try (Connection connection = Database.getConnection()) {
      String query = "UPDATE " + UsersTable.NAME + " SET password=? WHERE id=?";
      try (PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setBytes(1, password.getBytes());
        ps.setInt(2, user.getId());

        ClusterManager.getInstance().executeUpdate(ps);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public UserList list(User owner) throws UserNotExistException, ClusterException {
    return list(null, null, null, null, owner);
  }

  public UserList list(Filters filters, Sort sort, Offset offset, Limit limit, User owner) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return list(connection, filters, sort, offset, limit, owner);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public UserList list(Connection connection, Filters filters, Sort sort, Offset offset, Limit limit, User owner) throws ClusterException {
    Logger.fine("User list");

    String where = QueryHelper.getWhere(filters);

    long sqlOffsetValue = 0;
    if (offset != null) {
      sqlOffsetValue = offset.getValue();
    }
    long sqlLimitValue = UserList.MAX_PAGE_SIZE;
    if (limit != null) {
      sqlLimitValue = limit.getValue();
    }

    String sqlSort = QueryHelper.getOrderString(sort, "siteName, eMailId", validSortColumns);

    String sqlLimit = " LIMIT " + sqlOffsetValue + ", " + sqlLimitValue;

    String query
            = "SELECT u.id, u.site AS siteId, s.name AS siteName, e.id AS eMailId, p.id AS personId, e.address AS address, creationDate, activated, activated, passwordRecoveryUUID, passwordRecoveryDate "
            + "FROM " + UsersTable.NAME + " AS u "
            + "LEFT JOIN " + SitesTable.NAME + " AS s ON u.site = s.id "
            + "LEFT JOIN " + EMailsTable.NAME + " AS e ON u.eMail = e.id "
            + "LEFT JOIN " + PeopleTable.NAME + " AS p ON e.personId = p.id "
            + where + sqlSort + sqlLimit;
    ResultSet rs = null;
    UserList list;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      QueryHelper.setFiltersValues(filters, ps);
      rs = ClusterManager.getInstance().executeQuery(ps);
      list = new UserList(offset == null ? 0 : offset.getValue(), limit == null ? 0 : limit.getValue());
      while (rs.next()) {
        int id = rs.getInt("id");
        int siteId = rs.getInt("siteId");
        String siteName = rs.getString("siteName");
        int eMailId = rs.getInt("eMailId");
        int personId = rs.getInt("personId");
        String address = rs.getString("address");
        EMail eMail = new EMail(eMailId, personId, address);
        Date creationDate = rs.getDate("creationDate");
        boolean activated = rs.getBoolean("activated");
        String passwordRecoveryUUID = rs.getString("passwordRecoveryUUID");
        Date passwordRecoveryDate = rs.getDate("passwordRecoveryDate");
        UserForList user = new UserForList(id, siteId, siteName, eMail, creationDate, activated);
        list.add(user);
      }
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
  }

  public Message getRegistrationRetryAlertEMailData(String address) throws IOException, ClusterException {
    try (Connection connection = Database.getConnection()) {

      Person person = PeopleManager.getInstance().getByEMailAddress(connection, address);

      EMailTemplate emailRegistrationRetryAlertTemplate = TemplatesManager.getInstance().getEMailRegistrationRetryAlertTemplate(person.getLocale());

      emailRegistrationRetryAlertTemplate.set("name", person.getName());
      emailRegistrationRetryAlertTemplate.set("password.recover.page.url", Configuration.getInstance().get("password.recover.page.url"));
      emailRegistrationRetryAlertTemplate.set("site.name", Configuration.getInstance().get("site.name"));
      emailRegistrationRetryAlertTemplate.set("site.uri", Configuration.getInstance().get("site.uri"));

      EMail from = EMailManager.getInstance().get(connection, Configuration.getInstance().get("no.reply.email"));
      EMail to = EMailManager.getInstance().get(connection, address);

      return new Message(
              Configuration.getInstance().get("no.reply.name"),
              from,
              person.getName() + ' ' + person.getLastName(),
              to,
              emailRegistrationRetryAlertTemplate.getSubject(),
              emailRegistrationRetryAlertTemplate.getPlainText(),
              emailRegistrationRetryAlertTemplate.getHtmlText());
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Profiles getProfiles(User user) throws ClusterException {
    Logger.fine("User profile list for %s.", user);
    String query = "SELECT p.id, p.name, p.site FROM usersProfiles AS up LEFT JOIN profiles AS p ON up.profile = p.id WHERE up.user = ?";
    ResultSet rs = null;
    try (Connection connection = Database.getConnection(); PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, user.getId());
      rs = ClusterManager.getInstance().executeQuery(ps);
      Profiles list = new Profiles();
      while (rs.next()) {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        int site = rs.getInt("site");
        Profile profile = new Profile(id, name, site);
        list.add(profile);
      }
      return list;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public void add(Connection connection, User user, Profile profile, int ownerId) throws ClusterException {
    String query = "INSERT INTO " + UsersProfilesTable.NAME + " (user, profile, owner) VALUES (?, ?, ?)";
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      ps.setInt(1, user.getId());
      ps.setInt(2, profile.getId());
      ps.setInt(3, ownerId);

      ClusterManager.getInstance().executeUpdate(ps);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public int getTotal(Filters filters, Sort sort, Offset offset, Limit limit, User owner) throws ClusterException {
    return QueryHelper.getTotal(UsersTable.NAME, UserList.MAX_PAGE_SIZE, "eMailId", validSortColumns, filters, sort, offset, limit, owner);
  }
}
