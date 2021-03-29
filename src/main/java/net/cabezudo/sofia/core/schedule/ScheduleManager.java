package net.cabezudo.sofia.core.schedule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.16
 */
public class ScheduleManager {

  private static final ScheduleManager INSTANCE = new ScheduleManager();

  private ScheduleManager() {
    // Nothing to do here
  }

  public static ScheduleManager getInstance() {
    return INSTANCE;
  }

  public int add(Connection connection) throws ClusterException {
    String query = "INSERT INTO " + TimeEntriesTable.DATABASE_NAME + "." + TimeEntriesTable.NAME + " () VALUES ()";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ClusterManager.getInstance().executeUpdate(ps);
      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        return rs.getInt(1);
      }
      throw new SofiaRuntimeException("Can't get the generated key");
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }
}
