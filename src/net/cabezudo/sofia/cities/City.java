package net.cabezudo.sofia.cities;

import net.cabezudo.sofia.states.State;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class City {

  private final int id;
  private final State state;
  private final String name;

  public City(int id, State state, String name) {
    this.id = id;
    this.state = state;
    this.name = name;
  }

  @Override
  public String toString() {
    return "[id = " + getId() + ", state = " + getState() + ", name = " + getName() + "]";
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
