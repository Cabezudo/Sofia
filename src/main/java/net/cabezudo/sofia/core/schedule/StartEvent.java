package net.cabezudo.sofia.core.schedule;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.18
 */
public class StartEvent extends Event {

  public StartEvent(int day, Hour time) {
    super(START, day, time);
  }

}
