package net.cabezudo.sofia.food;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.02
 */
public class DishGroup {

  private final int id;
  private final Category category;
  private final String name;

  public DishGroup(int id, Category category, String name) {
    this.id = id;
    this.category = category;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Category getCategory() {
    return category;
  }
}
