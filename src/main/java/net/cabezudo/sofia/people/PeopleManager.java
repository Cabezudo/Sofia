package net.cabezudo.sofia.people;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.database.sql.Database;
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

  public Person create(String name, String lastName, User owner) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return create(connection, name, lastName, owner);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Person create(Connection connection, String name, String lastName, User owner) throws ClusterException {
    String query = "INSERT INTO " + PeopleTable.DATABASE_NAME + "." + PeopleTable.NAME + " (name, lastName, owner) VALUES (?, ?, ?)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ps.setString(1, name);
      ps.setString(2, lastName);
      ps.setInt(3, owner.getId());
      ClusterManager.getInstance().executeUpdate(ps);
      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        EMails eMails = new EMails();
        return new Person(id, name, lastName, eMails, owner);
      } else {
        throw new SofiaRuntimeException("Key not generated.");
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public void setPrimaryEMail(Connection connection, Person person, EMail eMail) throws ClusterException {
    if (eMail == null) {
      return;
    }
    if (eMail.getId() == 0) {
      throw new SofiaRuntimeException("The email id is 0.");
    }
    String query = "UPDATE " + PeopleTable.DATABASE_NAME + "." + PeopleTable.NAME + " SET primaryEMailId = ? WHERE id = ?";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setLong(1, eMail.getId());
      ps.setLong(2, person.getId());
      ClusterManager.getInstance().executeUpdate(ps);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }

  }

  public Person getByEMailAddress(String address) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return getByEMailAddress(connection, address);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Person getByEMailAddress(Connection connection, String address) throws ClusterException {
    Logger.fine("Get person using the email address'" + address + "'.");

    String query
            = "SELECT p.id, name, lastName, primaryEMailId, owner "
            + "FROM " + PeopleTable.DATABASE_NAME + "." + PeopleTable.NAME + " AS p "
            + "LEFT JOIN " + EMailsTable.DATABASE_NAME + "." + EMailsTable.NAME + " AS e ON p.id = e.personId "
            + "WHERE address = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, address);
      rs = ClusterManager.getInstance().executeQuery(ps);
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
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    return null;
  }

  public Person get(int id) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, id);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Person get(Connection connection, int id) throws ClusterException {
    Logger.fine("Get person using the id " + id + ".");

    String query
            = "SELECT p.id, name, lastName, primaryEMailId, owner "
            + "FROM " + PeopleTable.DATABASE_NAME + "." + PeopleTable.NAME + " AS p "
            + "LEFT JOIN " + EMailsTable.DATABASE_NAME + "." + EMailsTable.NAME + " AS e ON p.id = e.personId "
            + "WHERE p.id = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, id);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        String name = rs.getString("name");
        String lastName = rs.getString("lastName");
        int primaryEMailId = rs.getInt("primaryEMailId");
        EMails eMails = EMailManager.getInstance().getByPersonId(id);
        eMails.setPrimaryEMailById(primaryEMailId);
        User owner = UserManager.getInstance().get(connection, id);
        return new Person(id, name, lastName, eMails, owner);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    return null;
  }

  public EMail addEMailAddress(Person person, String address) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return EMailManager.getInstance().create(connection, person.getId(), address);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public PeopleList list(User owner) throws UserNotExistException {
    Logger.fine("Users list");
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
