package net.cabezudo.sofia.clients;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import net.cabezudo.sofia.core.api.options.OptionValue;
import net.cabezudo.sofia.core.list.Filters;
import net.cabezudo.sofia.core.list.Limit;
import net.cabezudo.sofia.core.list.Offset;
import net.cabezudo.sofia.core.list.Sort;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.database.QueryHelper;
import net.cabezudo.sofia.core.database.ValidSortColumns;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserNotExistException;
import net.cabezudo.sofia.core.users.UsersTable;
import net.cabezudo.sofia.emails.EMail;
import net.cabezudo.sofia.emails.EMailManager;
import net.cabezudo.sofia.emails.EMails;
import net.cabezudo.sofia.emails.EMailsTable;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.people.PeopleManager;
import net.cabezudo.sofia.people.PeopleTable;
import net.cabezudo.sofia.people.Person;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.13
 */
public class ClientManager {

  private final static ClientManager INSTANCE = new ClientManager();
  private final ValidSortColumns validSortColumns = new ValidSortColumns("personId", "name", "lastName", "address");

  private ClientManager() {
    // Nothing to do here
  }

  public static ClientManager getInstance() {
    return INSTANCE;
  }

  public Client create(String name, String lastName, User owner) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      try {
        connection.setAutoCommit(false);

        Person person;
        Client client;
        person = PeopleManager.getInstance().create(connection, name, lastName, owner);
        client = ClientManager.getInstance().create(connection, person);
        connection.setAutoCommit(true);
        return client;
      } catch (ClusterException | UserNotExistException e) {
        connection.rollback();
        throw new ClusterException(e);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  private Client create(Connection connection, Person person) throws UserNotExistException, ClusterException {
    String query = "INSERT INTO " + ClientTable.NAME + " (id) VALUES (?)";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setLong(1, person.getId());

      ClusterManager.getInstance().executeUpdate(ps);

      Client client = new Client(person);
      return client;
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Client get(String address, User owner) throws ClusterException {
    Logger.fine("Get client using the email address'" + address + "'.");

    ResultSet rs = null;
    String query
            = "SELECT p.id, name, lastName, primaryEMailId, owner "
            + "FROM " + PeopleTable.NAME + " AS p "
            + "LEFT JOIN " + EMailsTable.NAME + " AS e ON p.id = e.personId "
            + "LEFT JOIN " + ClientTable.NAME + " AS a ON p.id = a.id "
            + "WHERE address = ? AND owner = ?";
    try (Connection connection = Database.getConnection(); PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, address);
      ps.setInt(1, owner.getId());
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        return createClientFromResultSet(rs);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    return null;
  }

  public Client get(int id) throws ClusterException {
    Logger.fine("Get client using the id '" + id + "'.");

    ResultSet rs = null;
    String query
            = "SELECT p.id, name, lastName, primaryEMailId, owner "
            + "FROM " + PeopleTable.NAME + " AS p "
            + "LEFT JOIN " + ClientTable.NAME + " AS a ON p.id = a.id "
            + "WHERE p.id = ?";
    try (Connection connection = Database.getConnection(); PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, id);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        return createClientFromResultSet(rs);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    return null;
  }

  private Client createClientFromResultSet(ResultSet rs) throws SQLException, ClusterException {
    int id = rs.getInt("id");
    String name = rs.getString("name");
    String lastName = rs.getString("lastName");
    int primaryEMailId = rs.getInt("primaryEMailId");
    int owner = rs.getInt("owner");
    EMails eMails = EMailManager.getInstance().getByPersonId(id);
    eMails.setPrimaryEMailById(primaryEMailId);
    return new Client(id, name, lastName, eMails, owner);
  }

  public EMail add(Client client, String address) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      EMail eMail = EMailManager.getInstance().create(connection, client.getId(), address);
      if (client.getEMails().getPrimaryEMail() == null) {
        PeopleManager.getInstance().setPrimaryEMail(connection, client, eMail);
      }
      return eMail;
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  private String getWhere(Filters filter) {
    StringBuilder sb = new StringBuilder(" WHERE owner = ?");
    if (filter != null) {
      List<OptionValue> values = filter.getValues();
      values.forEach(value -> {
        if (value.isPositive()) {
          sb.append(" AND (name LIKE ? OR lastName LIKE ? OR address LIKE ?)");
        } else {
          sb.append(" AND name NOT LIKE ? AND lastName NOT LIKE ? AND address NOT LIKE ?");
        }
      });
    }
    return sb.toString();
  }

  public ClientList list(Filters filter, Sort sort, Offset offset, Limit limit, User owner) throws UserNotExistException, ClusterException {
    try (Connection connection = Database.getConnection()) {
      return list(connection, filter, sort, offset, limit, owner);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public ClientList list(Connection connection, Filters filter, Sort sort, Offset offset, Limit limit, User owner) throws UserNotExistException, ClusterException {
    Logger.fine("Client list");

    String where = getWhere(filter);
    long sqlOffsetValue = 0;
    if (offset != null) {
      sqlOffsetValue = offset.getValue();
    }
    long sqlLimitValue = ClientList.MAX_PAGE_SIZE;
    if (limit != null) {
      sqlLimitValue = limit.getValue();
    }
    String sqlSort = QueryHelper.getOrderString(sort, "lastName, name", validSortColumns);
    String sqlLimit = " LIMIT " + sqlOffsetValue + ", " + sqlLimitValue;

    try {
      connection.setAutoCommit(false);
      ClientList list;

      ResultSet rs = null;
      String query = "SELECT SQL_CALC_FOUND_ROWS p.id AS personId, name, lastName, primaryEMailId, address "
              + "FROM " + PeopleTable.NAME + " AS p "
              + "RIGHT JOIN " + EMailsTable.NAME + " AS e ON primaryEMailId = e.id "
              + "LEFT JOIN " + UsersTable.NAME + " AS u ON e.id = eMailId "
              + "RIGHT JOIN " + ClientTable.NAME + " AS a ON p.id = a.id"
              + where + sqlSort + sqlLimit;
      try (PreparedStatement ps = connection.prepareStatement(query);) {
        int i = 1;
        ps.setInt(i, owner.getId());
        i++;
        if (filter != null) {
          for (OptionValue ov : filter.getValues()) {
            ps.setString(i, "%" + ov.getValue() + "%");
            i++;
            ps.setString(i, "%" + ov.getValue() + "%");
            i++;
            ps.setString(i, "%" + ov.getValue() + "%");
            i++;
          }
        }
        rs = ClusterManager.getInstance().executeQuery(ps);

        list = new ClientList(offset == null ? 0 : offset.getValue(), limit == null ? ClientList.MAX_PAGE_SIZE : limit.getValue());
        while (rs.next()) {
          int personId = rs.getInt("personId");
          String name = rs.getString("name");
          String lastName = rs.getString("lastName");
          int primaryEMailId = rs.getInt("primaryEMailId");

          EMails eMails = new EMails();
          if (primaryEMailId != 0) {
            String address = rs.getString("address");
            EMail eMail = new EMail(primaryEMailId, personId, address);
            eMails.add(eMail);
            eMails.setPrimaryEMailById(primaryEMailId);
          }
          Client client = new Client(personId, name, lastName, eMails, owner.getId());

          list.add(client);
        }
      }

      query = "SELECT FOUND_ROWS() AS total";
      try (PreparedStatement ps = connection.prepareStatement(query);) {
        rs = ClusterManager.getInstance().executeQuery(ps);
        if (!rs.next()) {
          throw new SofiaRuntimeException("The select to count the number of clients fail.");
        }
        int total = rs.getInt("total");
        list.setTotal(total);
      } finally {
        ClusterManager.getInstance().close(rs);
      }

      connection.commit();

      return list;
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }
}
