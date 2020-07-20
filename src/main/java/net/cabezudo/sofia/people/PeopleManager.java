package net.cabezudo.sofia.people;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserManager;
import net.cabezudo.sofia.core.users.UserNotExistException;
import net.cabezudo.sofia.emails.EMail;
import net.cabezudo.sofia.emails.EMailManager;
import net.cabezudo.sofia.emails.EMails;
import net.cabezudo.sofia.emails.EMailsTable;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.08
 */
public class PeopleManager {

  private static PeopleManager instance;

  private PeopleManager() {
    // Nothing to do here
  }

  public static PeopleManager getInstance() {
    if (instance == null) {
      instance = new PeopleManager();
    }
    return instance;
  }

  public Person create(String name, String lastName, User owner) throws SQLException {
    try (Connection connection = Database.getConnection()) {
      return create(connection, name, lastName, owner);
    }
  }

  public Person create(Connection connection, String name, String lastName, User owner) throws SQLException {
    String query = "INSERT INTO " + PeopleTable.NAME + " (name, lastName, owner) VALUES (?, ?, ?)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, name);
      ps.setString(2, lastName);
      ps.setInt(3, owner.getId());
      Logger.fine(ps);
      ps.executeUpdate();

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        EMails eMails = new EMails();
        return new Person(id, name, lastName, eMails, owner);
      } else {
        throw new SofiaRuntimeException("Key not generated.");
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

  public void setPrimaryEMail(Connection connection, Person person, EMail eMail) throws SQLException {
    if (eMail == null) {
      return;
    }
    if (eMail.getId() == 0) {
      throw new SofiaRuntimeException("The email id is 0.");
    }
    String query = "UPDATE " + PeopleTable.NAME + " SET primaryEMailId = ? WHERE id = ?";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setLong(1, eMail.getId());
      ps.setLong(2, person.getId());
      Logger.fine(ps);
      ps.executeUpdate();
    }

  }

  public Person getByEMailAddress(String address) throws SQLException {
    try (Connection connection = Database.getConnection()) {
      return getByEMailAddress(connection, address);
    }
  }

  public Person getByEMailAddress(Connection connection, String address) throws SQLException {
    Logger.fine("Get person using the email address'" + address + "'.");

    String query
            = "SELECT p.id, name, lastName, primaryEMailId, owner "
            + "FROM " + PeopleTable.NAME + " AS p "
            + "LEFT JOIN " + EMailsTable.NAME + " AS e ON p.id = e.personId "
            + "WHERE address = ?";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setString(1, address);
      Logger.fine(ps);
      rs = ps.executeQuery();

      if (rs.next()) {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String lastName = rs.getString("lastName");
        int primaryEMailId = rs.getInt("primaryEMailId");
        int owner = rs.getInt("owner");
        EMails eMails = EMailManager.getInstance().getByPersonId(id);
        eMails.setPrimaryEMailById(primaryEMailId);
        return new Person(id, name, lastName, eMails, owner);
      }
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
    return null;
  }

  public Person get(int id) throws SQLException {
    try (Connection connection = Database.getConnection()) {
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
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setInt(1, id);
      Logger.fine(ps);
      rs = ps.executeQuery();

      if (rs.next()) {
        String name = rs.getString("name");
        String lastName = rs.getString("lastName");
        int primaryEMailId = rs.getInt("primaryEMailId");
        EMails eMails = EMailManager.getInstance().getByPersonId(id);
        eMails.setPrimaryEMailById(primaryEMailId);
        User owner = UserManager.getInstance().get(connection, id);
        return new Person(id, name, lastName, eMails, owner);
      }
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
    return null;
  }

  public EMail addEMailAddress(Person person, String address) throws SQLException {
    try (Connection connection = Database.getConnection()) {
      return EMailManager.getInstance().create(connection, person.getId(), address);
    }
  }

  public PeopleList list(User owner) throws SQLException, UserNotExistException {
    Logger.fine("Users list");
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
