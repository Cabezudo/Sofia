package net.cabezudo.sofia.core.database.sql;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.logger.Logger;

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

  public static Connection getConnection(String databaseName) throws SQLException {
    return getConnection(databaseName, 20);
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

  public static void createDatabase() throws ClusterException {
    createDatabase(Configuration.getInstance().getDatabaseName());
  }

  public static void createDatabase(String databaseName) throws ClusterException {
    Logger.info("Create database.");
    try (Connection connection = getConnection(null, 5); Statement statement = connection.createStatement();) {
      String query = "CREATE DATABASE IF NOT EXISTS " + databaseName;
      ClusterManager.getInstance().executeUpdate(statement, query);
      query = "SET @@global.time_zone='+00:00'";
      ClusterManager.getInstance().execute(statement, query);
      statement.execute(query);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public static void drop(String database) throws SQLException {
    Logger.info("Drop " + database + " database.");
    Connection connection = getConnection(null);
    try (Statement statement = connection.createStatement()) {
      String query = "DROP DATABASE IF EXISTS " + database;
      statement.executeUpdate(query);
    }
  }

  public static void restoreData() throws SQLException, FileNotFoundException {
    Connection connection = getConnection();
    File in = new File(Configuration.getInstance().get("database.backup.file.name"));
    Statement statement = null;
    try (Scanner scanner = new Scanner(in)) {

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

  public static void createTable(Connection connection, String[] queries) throws ClusterException {
    try {
      connection.setAutoCommit(false);
      for (String query : queries) {
        createTable(connection, query);
      }
      connection.setAutoCommit(true);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public static void createTable(Connection connection, String query) throws ClusterException {
    try (Statement statement = connection.createStatement()) {
      ClusterManager.getInstance().executeUpdate(statement, query);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public static boolean exist(String databaseName) throws ClusterException {
    ResultSet rs = null;
    boolean next;
    try (Connection connection = getConnection(null, 5); Statement statement = connection.createStatement()) {
      String query = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + databaseName + "'";
      rs = statement.executeQuery(query);
      next = rs.next();
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    return next;
  }
}
