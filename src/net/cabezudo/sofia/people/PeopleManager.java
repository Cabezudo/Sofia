package net.cabezudo.sofia.people;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.emails.EMail;
import net.cabezudo.sofia.emails.EMailManager;
import net.cabezudo.sofia.emails.EMails;
import net.cabezudo.sofia.emails.EMailsTable;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserManager;
import net.cabezudo.sofia.core.users.UserNotExistException;
import net.cabezudo.sofia.core.users.UsersTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.08
 */
public class PeopleManager {

  private static PeopleManager INSTANCE;

  private PeopleManager() {
    // Nothing to do here
  }

  public static PeopleManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new PeopleManager();
    }
    return INSTANCE;
  }

  public Person create(String name, String lastName, User owner) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return create(connection, name, lastName, owner);
    }
  }

  public Person create(Connection connection, String name, String lastName, User owner) throws SQLException {
    String query = "INSERT INTO " + PeopleTable.NAME + " (name, lastName, owner) VALUES (?, ?, ?)";
    PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    ps.setString(1, name);
    ps.setString(2, lastName);
    ps.setInt(3, owner.getId());
    Logger.fine(ps);
    ps.executeUpdate();

    ResultSet rs = ps.getGeneratedKeys();
    if (rs.next()) {
      int id = rs.getInt(1);
      Person person;
      EMails eMails = new EMails();
      person = new Person(id, name, lastName, eMails, owner);
      return person;
    } else {
      throw new RuntimeException("Key not generated.");
    }
  }

  public void setPrimaryEMail(Connection connection, Person person, EMail eMail) throws SQLException {
    if (eMail == null) {
      return;
    }
    if (eMail.getId() == 0) {
      throw new RuntimeException("The email id is 0.");
    }
    String query = "UPDATE " + PeopleTable.NAME + " SET primaryEMailId = ? WHERE id = ?";
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setLong(1, eMail.getId());
    ps.setLong(2, person.getId());
    Logger.fine(ps);
    ps.executeUpdate();

  }

  public Person getByEMailAddress(Site site, String address) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return getByEMailAddress(connection, site, address);
    }
  }

  public Person getByEMailAddress(Connection connection, Site site, String address) throws SQLException {
    Logger.fine("Get person using the email address'" + address + "'.");

    String query
            = "SELECT p.id, name, lastName, primaryEMailId, owner "
            + "FROM " + PeopleTable.NAME + " AS p "
            + "LEFT JOIN " + EMailsTable.NAME + " AS e ON p.id = e.personId "
            + "WHERE address = ?";
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setString(1, address);
    Logger.fine(ps);
    ResultSet rs = ps.executeQuery();

    if (rs.next()) {
      int id = rs.getInt("id");
      String name = rs.getString("name");
      String lastName = rs.getString("lastName");
      int primaryEMailId = rs.getInt("primaryEMailId");
      int owner = rs.getInt("owner");
      EMails eMails = EMailManager.getInstance().getByPersonId(id);
      eMails.setPrimaryEMailById(primaryEMailId);
      Person person = new Person(id, name, lastName, eMails, owner);
      return person;
    }
    return null;
  }

  public Person get(int id) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return get(connection, id);
    }
  }

  public Person get(Connection connection, int id) throws SQLException {
    Logger.fine("Get person using the id " + id + ".");

    String query
            = "SELECT p.id, name, lastName, primaryEMailId, owner "
            + "FROM " + PeopleTable.NAME + " AS p "
            + "LEFT JOIN " + EMailsTable.NAME + " AS e ON p.id = e.personId "
            + "WHERE p.id = ?";
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, id);
    Logger.fine(ps);
    ResultSet rs = ps.executeQuery();

    if (rs.next()) {
      String name = rs.getString("name");
      String lastName = rs.getString("lastName");
      int primaryEMailId = rs.getInt("primaryEMailId");
      EMails eMails = EMailManager.getInstance().getByPersonId(id);
      eMails.setPrimaryEMailById(primaryEMailId);
      User owner = UserManager.getInstance().getById(connection, id);
      Person person = new Person(id, name, lastName, eMails, owner);
      return person;
    }
    return null;
  }

  public EMail addEMailAddress(Person person, String address) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      return EMailManager.getInstance().create(connection, person.getId(), address);
    }
  }

  public PeopleList list(User owner) throws SQLException, UserNotExistException {
    Logger.fine("Users list");

    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      String query = "SELECT count(p.id) AS total "
              + "FROM " + PeopleTable.NAME + " AS p "
              + "LEFT JOIN " + EMailsTable.NAME + " AS e ON p.id = personId "
              + "LEFT JOIN " + UsersTable.NAME + " AS u ON e.id = eMailId";
      PreparedStatement ps = connection.prepareStatement(query);
      Logger.fine(ps);
      ResultSet rs = ps.executeQuery();

      while (!rs.next()) {
        throw new RuntimeException("The select to count the number of people fail.");
      }

      long total = rs.getInt("total");

      query = "SELECT p.id AS personId, name, lastName, e.id AS eMailId, address "
              + "FROM " + PeopleTable.NAME + " AS p "
              + "LEFT JOIN " + EMailsTable.NAME + " AS e ON p.id = personId "
              + "LEFT JOIN " + UsersTable.NAME + " AS u ON e.id = eMailId";
      ps = connection.prepareStatement(query);
      Logger.fine(ps);
      rs = ps.executeQuery();

      PeopleList list = new PeopleList();
      while (rs.next()) {
        int personId = rs.getInt("personId");
        String name = rs.getString("name");
        String lastName = rs.getString("lastName");
        int eMailId = rs.getInt("eMailId");
        EMails eMails = new EMails();
        if (eMailId > 0) {
          String address = rs.getString("address");
          EMail eMail = new EMail(eMailId, personId, address);
          eMails.add(eMail);
        }
        Person person = new Person(personId, name, lastName, eMails, owner);
        list.add(person);
      }
      return list;
    }
  }
}
