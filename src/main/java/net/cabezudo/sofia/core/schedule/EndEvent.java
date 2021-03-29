package net.cabezudo.sofia.core.schedule;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.19
 */
public class EndEvent extends Event {

  public EndEvent(int day, Hour time) {
    super(END, day, time);
  }

}
