package net.cabezudo.sofia.core.schedule;

import java.sql.SQLException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.18
 */
public class WeekTime extends AbstractTime {

  public WeekTime(int id, int index, int start, int end) throws SQLException {
    super(id, TimeTypeManager.getInstance().get("week"), index, start, end);
  }

  @Override
  public boolean dayIs(int day) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}