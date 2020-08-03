package net.cabezudo.sofia.core.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import net.cabezudo.sofia.addresses.AddressesTable;
import net.cabezudo.sofia.cities.CitiesTable;
import net.cabezudo.sofia.clients.ClientTable;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.sites.SitesTable;
import net.cabezudo.sofia.core.sites.domainname.DomainNamesTable;
import net.cabezudo.sofia.core.users.UsersTable;
import net.cabezudo.sofia.core.users.permission.PermissionTypesTable;
import net.cabezudo.sofia.core.users.permission.PermissionsPermissionTypesTable;
import net.cabezudo.sofia.core.users.permission.PermissionsTable;
import net.cabezudo.sofia.core.users.permission.ProfilesPermissionsTable;
import net.cabezudo.sofia.core.users.profiles.ProfilesTable;
import net.cabezudo.sofia.core.users.profiles.UsersProfilesTable;
import net.cabezudo.sofia.core.webusers.WebUserDataTable;
import net.cabezudo.sofia.countries.CountriesTable;
import net.cabezudo.sofia.emails.EMailsTable;
import net.cabezudo.sofia.languages.LanguagesTable;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.municipalities.MunicipalitiesTable;
import net.cabezudo.sofia.people.PeopleTable;
import net.cabezudo.sofia.phonenumbers.PhoneNumbersTable;
import net.cabezudo.sofia.postalcodes.PostalCodesTable;
import net.cabezudo.sofia.settlements.SettlementTypesTable;
import net.cabezudo.sofia.settlements.SettlementsTable;
import net.cabezudo.sofia.states.StatesTable;
import net.cabezudo.sofia.streets.StreetsTable;
import net.cabezudo.sofia.zones.ZonesTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.16
 */
public class Database {

  private Database() {
    // Nothing to do here. Utility classes should not have public constructors.
  }

  public static Connection getConnection() throws SQLException {
    return getConnection(Configuration.getInstance().getDatabaseName(), 20);
  }

  public static Connection getConnection(String databaseName, int maxReconnects) throws SQLException {
    if (maxReconnects == 0) {
      maxReconnects = 20;
    }
    String databaseHostname = Configuration.getInstance().getDatabaseHostname();
    String databasePort = Configuration.getInstance().getDatabasePort();
    String username = Configuration.getInstance().getDatabaseUser();
    String password = Configuration.getInstance().getDatabasePassword();

    String url;
    String queryString = "?verifyServerCertificate=false&useSSL=false&characterEncoding=utf8&useUnicode=true&serverTimezone=UTC&allowPublicKeyRetrieval=true&autoReconnect=true&failOverReadOnly=false&maxReconnects=" + maxReconnects;
    if (databaseName == null) {
      url = "jdbc:mysql://" + databaseHostname + ":" + databasePort + queryString;
    } else {
      url = "jdbc:mysql://" + databaseHostname + ":" + databasePort + "/" + databaseName + queryString;
    }
    return DriverManager.getConnection(url, username, password);
  }

  public static void createDatabase() throws SQLException {
    Logger.info("Create database.");
    Statement statement = null;
    try ( Connection connection = getConnection(null, 5)) {
      statement = connection.createStatement();

      String query = "CREATE DATABASE IF NOT EXISTS " + Configuration.getInstance().getDatabaseName();
      statement.executeUpdate(query);

      query = "SET @@global.time_zone='+00:00'";
      statement.execute(query);
    } finally {
      if (statement != null) {
        statement.close();
      }
    }

    try ( Connection connection = getConnection()) {
      createTables(connection);
    }

  }

  public static void drop() throws SQLException {
    Logger.info("Drop all database.");
    Connection connection = getConnection();
    try ( Statement statement = connection.createStatement()) {
      String query = "DROP DATABASE IF EXISTS " + Configuration.getInstance().getDatabaseName();
      statement.executeUpdate(query);
    }
  }

  public static void restoreData() throws SQLException, FileNotFoundException {
    Connection connection = getConnection();
    File in = new File(Configuration.getInstance().get("database.backup.file.name"));
    Statement statement = null;
    try ( Scanner scanner = new Scanner(in)) {

      statement = connection.createStatement();
      statement.execute("SET FOREIGN_KEY_CHECKS=0");

      StringBuilder sb = new StringBuilder();
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();

        if (line.startsWith("--")) {
          continue;
        }
        if (line.startsWith("/*")) {
          continue;
        }
        if (line.isEmpty()) {
          continue;
        }
        sb.append(line);

        if (line.endsWith(";")) {
          statement.executeUpdate(sb.toString());
          sb = new StringBuilder();
        }
      }
      statement.execute("SET FOREIGN_KEY_CHECKS=1");
    } finally {
      if (statement != null) {
        statement.close();
      }
    }
  }

  private static void createTables(Connection connection) throws SQLException {
    Logger.info("Create database tables.");
    createTable(connection, SitesTable.CREATION_QUERY);
    createTable(connection, DomainNamesTable.CREATION_QUERY);
    createTable(connection, PeopleTable.CREATION_QUERY);
    createTable(connection, EMailsTable.CREATION_QUERY);
    createTable(connection, UsersTable.CREATION_QUERY);
    createTable(connection, WebUserDataTable.CREATION_QUERY);
    createTable(connection, ClientTable.CREATION_QUERY);
    createTable(connection, LanguagesTable.CREATION_QUERY);
    createTable(connection, PhoneNumbersTable.CREATION_QUERY);
    createTable(connection, CountriesTable.CREATION_QUERY);
    createTable(connection, StatesTable.CREATION_QUERY);
    createTable(connection, MunicipalitiesTable.CREATION_QUERY);
    createTable(connection, CitiesTable.CREATION_QUERY);
    createTable(connection, SettlementTypesTable.CREATION_QUERY);
    createTable(connection, ZonesTable.CREATION_QUERY);
    createTable(connection, SettlementsTable.CREATION_QUERY);
    createTable(connection, PostalCodesTable.CREATION_QUERY);
    createTable(connection, StreetsTable.CREATION_QUERY);
    createTable(connection, AddressesTable.CREATION_QUERY);
    createTable(connection, ProfilesTable.CREATION_QUERY);
    createTable(connection, UsersProfilesTable.CREATION_QUERY);
    createTable(connection, PermissionsTable.CREATION_QUERY);
    createTable(connection, ProfilesPermissionsTable.CREATION_QUERY);
    createTable(connection, PermissionTypesTable.CREATION_QUERY);
    createTable(connection, PermissionsPermissionTypesTable.CREATION_QUERY);
  }

  private static void createTable(Connection connection, String query) throws SQLException {
    try ( Statement statement = connection.createStatement()) {
      Logger.debug("Create table using: " + query);
      statement.executeUpdate(query);
    }
  }

  public static boolean exist(String databaseName) throws SQLException {
    ResultSet rs = null;
    boolean next;
    try ( Connection connection = getConnection(null, 5);  Statement statement = connection.createStatement()) {
      String query = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + databaseName + "'";
      rs = statement.executeQuery(query);
      next = rs.next();
    } finally {
      if (rs != null) {
        rs.close();
      }
    }
    return next;
  }
}
