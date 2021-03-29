package net.cabezudo.sofia.core.schedule;

import net.cabezudo.sofia.core.cluster.ClusterException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.18
 */
public class DayTime extends AbstractTime {

  public DayTime(int index, Hour from, Hour to) throws ClusterException {
    super(TimeType.DAY, index, from, to);
  }

  @Override
  public boolean dayIs(int day) {
    return getIndex() == day;
  }
}
