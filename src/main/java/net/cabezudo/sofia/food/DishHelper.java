package net.cabezudo.sofia.food;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.02
 */
public class DishHelper {

  private int id;
  private DishGroup dishGroup;
  private String name;
  private String description;
  private String image;
  private Dishes dishes;
  private Allergens allergens;
  private int calories;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public DishGroup getDishGroup() {
    return dishGroup;
  }

  public void setDishGroup(DishGroup dishGroup) {
    this.dishGroup = dishGroup;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public Dishes getDishes() {
    return dishes;
  }

  public void addDishes(Dishes dishes) {
    this.dishes = dishes;
  }

  public Allergens getAllergens() {
    return allergens;
  }

  public void addAllergens(Allergen allergen) {
    this.allergens.add(allergen);
  }

  public int getCalories() {
    return calories;
  }

  public void setCalories(int calories) {
    this.calories = calories;
  }
}
