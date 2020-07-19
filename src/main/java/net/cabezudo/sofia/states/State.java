package net.cabezudo.sofia.states;

import net.cabezudo.sofia.countries.Country;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class State {

  private final int id;
  private final Country country;
  private final String name;

  public State(int id, Country country, String name) {
    this.id = id;
    this.country = country;
    this.name = name;
  }

  @Override
  public String toString() {
    return "[id = " + getId() + ", country= " + getCountry() + ", name = " + getName() + "]";
  }

  public int getId() {
    return id;
  }

  public Country getCountry() {
    return country;
  }

  public String getName() {
    return name;
  }
}
