package net.cabezudo.sofia.core.schedule;

import net.cabezudo.sofia.core.cluster.ClusterException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.18
 */
public class MonthTime extends AbstractTime {

  public MonthTime(int index, Hour from, Hour to) throws ClusterException {
    super(TimeType.MONTH, index, from, to);
  }

  @Override
  public boolean dayIs(int day) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
