package net.cabezudo.sofia.food.helpers;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.04
 */
public class AllergenHelper {

  private final int id;
  private final String name;

  public AllergenHelper(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;

  }

  public String getName() {
    return name;
  }

}
