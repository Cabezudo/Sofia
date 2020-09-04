package net.cabezudo.sofia.food.helpers;

import net.cabezudo.sofia.core.money.Money;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.02
 */
public class DishHelper {

  private final int id;
  private final String name;
  private final String description;
  private final String imageName;
  private AllergensHelper allergens;
  private final int calories;
  private final Money price;

  DishHelper(int id, String name, String description, String imageName, AllergensHelper allergens, int calories, Money price) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.imageName = imageName;
    this.allergens = allergens;
    this.calories = calories;
    this.price = price;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getImageName() {
    return imageName;
  }

  public AllergensHelper getAllergens() {
    return allergens;
  }

  void addAllergens(AllergenHelper allergen) {
    this.allergens.add(allergen);
  }

  public int getCalories() {
    return calories;
  }

  public Money getPrice() {
    return price;
  }
}
