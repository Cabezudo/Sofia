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
    String query = "INSERT INTO " + TimeEntriesTable.DATABASE + "." + TimeEntriesTable.NAME + " () VALUES ()";
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

  public int addTime(Connection connection, int scheduleId, Day day, Hour start, Hour end) throws ClusterException {
    ResultSet rs = null;
    TimeType timeType = TimeTypeManager.getInstance().get(connection, "day");
    String query = "INSERT INTO " + TimesTable.DATABASE + "." + TimesTable.NAME + " (`entry`, `type`, `index`, `start`, `end`) VALUES (?, ?, ?, ?, ?)";
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ps.setInt(1, scheduleId);
      ps.setInt(2, timeType.getId());
      ps.setInt(3, day.getId());
      ps.setInt(4, start.getTime());
      ps.setInt(5, end.getTime());
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
