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
import net.cabezudo.sofia.core.configuration.Environment;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.02.20
 */
// TODO implement AutoCloseable
public class ClusterManager {

  private static final ClusterManager INSTANCE = new ClusterManager();

  private int counter;
  private long lastTimestamp;

  public static ClusterManager getInstance() {
    return INSTANCE;
  }

  private ClusterManager() {
    // Nothng to do here. Just protect the constructor
  }

  public String getEntryLog(PreparedStatement ps) throws IOException {
    String psString = ps.toString();
    int i = psString.indexOf(": ");
    String query = psString.substring(i + 2);
    return getEntryLog(query);
  }

  public String getEntryLog(String query) throws IOException {
    long newTimestamp = new Date().getTime();
    if (lastTimestamp == newTimestamp) {
      counter++;
    } else {
      counter = 0;
    }
    long id = (new Date().getTime() * 100) + counter;
    String log = id + " : " + query + "\n";
    lastTimestamp = newTimestamp;
    return log;
  }

  private void writeUpdateLog(PreparedStatement ps) throws IOException {
    String log = getEntryLog(ps);
    writeUpdateLog(log);
  }

  private void writeUpdateLog(String log) throws IOException {
    File filenameForUpdates = Configuration.getInstance().getClusterFileLogPath().toFile();
    try (FileWriter fw = new FileWriter(filenameForUpdates, true); BufferedWriter writer = new BufferedWriter(fw);) {
      writer.write(log);
    }
  }

  private void writeSelectLog(PreparedStatement ps) throws IOException {
    String log = getEntryLog(ps);
    writeSelectLog(log);
  }

  private void writeSelectLog(String log) throws IOException {
    if (Environment.getInstance().isDevelopment()) {
      File filenameForSelects = Configuration.getInstance().getClusterFileSelectLogPath().toFile();
      try (FileWriter fw = new FileWriter(filenameForSelects, true); BufferedWriter writer = new BufferedWriter(fw);) {
        writer.write(log);
      }
    }
  }

  public ResultSet executeQuery(PreparedStatement ps) throws ClusterException {
    try {
      writeSelectLog(ps);
      return ps.executeQuery();
    } catch (SQLException | IOException e) {
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
      writeUpdateLog(query);
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

  public void executeUpdate(PreparedStatement ps) throws ClusterException {
    try {
      writeUpdateLog(ps);
      ps.executeUpdate();
    } catch (SQLException | IOException e) {
      throw new ClusterException(e);
    }
  }
}
