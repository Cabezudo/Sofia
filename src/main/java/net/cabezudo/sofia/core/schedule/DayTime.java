package net.cabezudo.sofia.core.schedule;

import java.sql.SQLException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.18
 */
public class DayTime extends AbstractTime {

  public DayTime(int id, int index, int start, int end) throws SQLException {
    super(id, TimeTypeManager.getInstance().get("day"), index, start, end);
  }

  @Override
  public boolean dayIs(int day) {
    return getIndex() == day;
  }
}
