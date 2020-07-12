package net.cabezudo.sofia.clients;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import net.cabezudo.sofia.core.QueryHelper;
import net.cabezudo.sofia.core.api.options.OptionValue;
import net.cabezudo.sofia.core.api.options.list.Filters;
import net.cabezudo.sofia.core.api.options.list.Limit;
import net.cabezudo.sofia.core.api.options.list.Offset;
import net.cabezudo.sofia.core.api.options.list.Sort;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserNotExistException;
import net.cabezudo.sofia.core.users.UsersTable;
import net.cabezudo.sofia.emails.EMail;
import net.cabezudo.sofia.emails.EMailManager;
import net.cabezudo.sofia.emails.EMails;
import net.cabezudo.sofia.emails.EMailsTable;
import net.cabezudo.sofia.people.PeopleManager;
import net.cabezudo.sofia.people.PeopleTable;
import net.cabezudo.sofia.people.Person;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.13
 */
public class ClientManager {

  private static ClientManager INSTANCE;

  private ClientManager() {
    // Nothing to do here
  }

  public static ClientManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ClientManager();
    }
    return INSTANCE;
  }

  public Client create(String name, String lastName, User owner) throws SQLException, UserNotExistException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      connection.setAutoCommit(false);
      Person person = PeopleManager.getInstance().create(connection, name, lastName, owner);
      Client client = ClientManager.getInstance().create(connection, person);
      connection.setAutoCommit(true);

      return client;
    }
  }

  private Client create(Connection connection, Person person) throws SQLException, UserNotExistException {
    String query = "INSERT INTO " + ClientTable.NAME + " (id) VALUES (?)";
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setLong(1, person.getId());
    Logger.fine(ps);
    ps.executeUpdate();
    return new Client(person);
  }

  public Client get(String address, User owner) throws SQLException {
    Logger.fine("Get client using the email address'" + address + "'.");

    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      String query
              = "SELECT p.id, name, lastName, primaryEMailId, owner "
              + "FROM " + PeopleTable.NAME + " AS p "
              + "LEFT JOIN " + EMailsTable.NAME + " AS e ON p.id = e.personId "
              + "LEFT JOIN " + ClientTable.NAME + " AS a ON p.id = a.id "
              + "WHERE address = ? AND owner = ?";
      PreparedStatement ps = connection.prepareStatement(query);
      ps.setString(1, address);
      ps.setInt(1, owner.getId());
      Logger.fine(ps);
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        return createClientFromResultSet(rs);
      }
      return null;
    }
  }

  public Client get(int id) throws SQLException {
    Logger.fine("Get client using the id '" + id + "'.");

    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      String query
              = "SELECT p.id, name, lastName, primaryEMailId, owner "
              + "FROM " + PeopleTable.NAME + " AS p "
              + "LEFT JOIN " + ClientTable.NAME + " AS a ON p.id = a.id "
              + "WHERE p.id = ?";
      PreparedStatement ps = connection.prepareStatement(query);
      ps.setInt(1, id);
      Logger.fine(ps);
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        return createClientFromResultSet(rs);
      }
      return null;
    }
  }

  private Client createClientFromResultSet(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    String name = rs.getString("name");
    String lastName = rs.getString("lastName");
    int primaryEMailId = rs.getInt("primaryEMailId");
    int owner = rs.getInt("owner");
    EMails eMails = EMailManager.getInstance().getByPersonId(id);
    eMails.setPrimaryEMailById(primaryEMailId);
    Client client = new Client(id, name, lastName, eMails, owner);
    return client;
  }

  public EMail add(Client client, String address) throws SQLException {
    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      EMail eMail = EMailManager.getInstance().create(connection, client.getId(), address);
      if (client.getEMails().getPrimaryEMail() == null) {
        PeopleManager.getInstance().setPrimaryEMail(connection, client, eMail);
      }
      return eMail;
    }
  }

  public ClientList list(Filters filter, Sort sort, Offset offset, Limit limit, User owner) throws SQLException, UserNotExistException {
    Logger.fine("Client list");

    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {

      String where = " WHERE owner = ?";
      if (filter != null) {
        List<OptionValue> values = filter.getValues();
        for (OptionValue value : values) {
          if (value.isPositive()) {
            where += " AND (name LIKE ? OR lastName LIKE ? OR address LIKE ?)";
          } else {
            where += " AND name NOT LIKE ? AND lastName NOT LIKE ? AND address NOT LIKE ?";
          }
        }
      }

      long sqlOffsetValue = 0;
      if (offset != null) {
        sqlOffsetValue = offset.getValue();
      }
      long sqlLimitValue = ClientList.MAX;
      if (limit != null) {
        sqlLimitValue = limit.getValue();
      }

      String sqlSort = QueryHelper.getOrderString(sort, "lastName, name", new String[]{"personId", "name", "lastName", "address"});

      String sqlLimit = " LIMIT " + sqlOffsetValue + ", " + sqlLimitValue;

      String query = "SELECT SQL_CALC_FOUND_ROWS p.id AS personId, name, lastName, primaryEMailId, address "
              + "FROM " + PeopleTable.NAME + " AS p "
              + "RIGHT JOIN " + EMailsTable.NAME + " AS e ON primaryEMailId = e.id "
              + "LEFT JOIN " + UsersTable.NAME + " AS u ON e.id = eMailId "
              + "RIGHT JOIN " + ClientTable.NAME + " AS a ON p.id = a.id"
              + where + sqlSort + sqlLimit;
      PreparedStatement ps = connection.prepareStatement(query);
      ps.setInt(1, owner.getId());
      if (filter != null) {
        int i = 1;
        for (OptionValue ov : filter.getValues()) {
          ps.setString(i, "%" + ov.getValue() + "%");
          i++;
          ps.setString(i, "%" + ov.getValue() + "%");
          i++;
          ps.setString(i, "%" + ov.getValue() + "%");
          i++;
        }
      }

      Logger.fine(ps);
      ResultSet rs = ps.executeQuery();

      ClientList list = new ClientList(offset == null ? 0 : offset.getValue(), limit == null ? ClientList.MAX : limit.getValue());
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

      query = "SELECT FOUND_ROWS() AS total";
      ps = connection.prepareStatement(query);
      Logger.fine(ps);
      rs = ps.executeQuery();
      if (!rs.next()) {
        throw new RuntimeException("The select to count the number of clients fail.");
      }
      int total = rs.getInt("total");

      list.setTotal(total);

      return list;
    }
  }
}
