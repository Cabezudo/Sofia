package net.cabezudo.sofia.core.schedule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.logger.Logger;

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

  public int add(Connection connection) throws SQLException {
    String query = "INSERT INTO " + TimeEntriesTable.DATABASE + "." + TimeEntriesTable.NAME + " () VALUES ()";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      Logger.fine(ps);
      ps.executeUpdate();

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        return rs.getInt(1);
      }
      throw new SofiaRuntimeException("Can't get the generated key");
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
  }

  public int addTime(Connection connection, int scheduleId, Day day, Hour start, Hour end) throws SQLException {

    TimeType timeType = TimeTypeManager.getInstance().get(connection, "day");

    String query = "INSERT INTO " + TimesTable.DATABASE + "." + TimesTable.NAME + " (`entry`, `type`, `index`, `start`, `end`) VALUES (?, ?, ?, ?, ?)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      ps.setInt(1, scheduleId);
      ps.setInt(2, timeType.getId());
      ps.setInt(3, day.getId());
      ps.setInt(4, start.getTime());
      ps.setInt(5, end.getTime());
      Logger.fine(ps);
      ps.executeUpdate();

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        return rs.getInt(1);
      }
      throw new SofiaRuntimeException("Can't get the generated key");
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
  }
}
