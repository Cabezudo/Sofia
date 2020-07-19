package net.cabezudo.sofia.municipalities;

import net.cabezudo.sofia.states.State;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class Municipality {

  private final int id;
  private final State state;
  private final String name;

  public Municipality(int id, State state, String name) {
    this.id = id;
    this.state = state;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public State getState() {
    return state;
  }

  public String getName() {
    return name;
  }

}
