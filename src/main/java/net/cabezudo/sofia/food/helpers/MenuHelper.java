package net.cabezudo.sofia.food.helpers;

import java.math.BigDecimal;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.04
 */
public class MenuHelper {

  private final CategoriesHelper categories = new CategoriesHelper();

  public void addCategory(int id, String name, ScheduleHelper schedule) {
    CategoryHelper category = new CategoryHelper(id, name, schedule);
    categories.add(category);
  }

  public void addDishGroup(int dishGroupId, int categoryId, String dishGroupName) {
    CategoryHelper category = categories.get(categoryId);
    category.add(new DishGroupHelper(dishGroupId, dishGroupName));
  }

  public void addDish(int categoryId, int id, int dishGroupId, String name, String description, String imageName, AllergensHelper allergens, int calories, String currencyCode, BigDecimal cost) {
    CategoryHelper category = categories.get(categoryId);
    DishGroupHelper dishGroup = category.getDishGroup(dishGroupId);
    dishGroup.add(id, name, description, imageName, allergens, calories, currencyCode, cost);
  }

  public CategoriesHelper getCategories() {
    return categories;
  }

}
