package net.cabezudo.sofia.core.cluster;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.configuration.RuntimeConfigurationException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.02.20
 */
public class ClusterManager {

  private static final ClusterManager INSTANCE = new ClusterManager();

  private int counter;
  private long lastTimestamp;
  private final BufferedWriter writer;

  public static ClusterManager getInstance() {
    return INSTANCE;
  }

  private ClusterManager() {
    File filename = Configuration.getInstance().getClusterFileLogPath().toFile();
    FileWriter fw;
    try {
      fw = new FileWriter(filename, true);
    } catch (IOException e) {
      throw new RuntimeConfigurationException(e);
    }
    writer = new BufferedWriter(fw);
  }

  public String getCommand(PreparedStatement ps) throws IOException, SQLException {
    String psString = ps.toString();
    int i = psString.indexOf(": ");
    String query = psString.substring(i + 2);
    return getCommand(query);
  }

  public String getCommand(String query) throws IOException, SQLException {

    long newTimestamp = new Date().getTime();
    if (lastTimestamp == newTimestamp) {
      counter++;
    } else {
      counter = 0;
    }
    long id = (new Date().getTime() * 100) + counter;
    String log = id + " : " + query + "\n";

    lastTimestamp = newTimestamp;
    writer.write(log);
    return log;
  }

  public void executeUpdate(PreparedStatement ps) throws ClusterException {
    try {
      getCommand(ps);
      ps.executeUpdate();
    } catch (SQLException | IOException e) {
      throw new ClusterException(e);
    }
  }

  public ResultSet executeQuery(PreparedStatement ps) throws ClusterException {
    try {
      return ps.executeQuery();
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public void close(ResultSet rs) throws ClusterException {
    try {
      if (rs != null) {
        rs.close();
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public void executeUpdate(Statement statement, String query) throws ClusterException {
    try {
      getCommand(query);
      statement.executeUpdate(query);
    } catch (SQLException | IOException e) {
      throw new ClusterException(e);
    }
  }

  public void execute(Statement statement, String query) throws ClusterException {
    try {
      statement.execute(query);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }
}
