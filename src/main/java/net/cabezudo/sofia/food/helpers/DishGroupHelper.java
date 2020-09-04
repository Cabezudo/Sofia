package net.cabezudo.sofia.food.helpers;

import java.math.BigDecimal;
import java.util.Currency;
import net.cabezudo.sofia.core.money.Money;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.04
 */
public class DishGroupHelper {

  private final int id;
  private final String name;
  private final DishesHelper dishes = new DishesHelper();

  public DishGroupHelper(int id, String name) {
    this.id = id;
    this.name = name;
  }

  DishGroupHelper() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public DishesHelper getDishes() {
    return dishes;
  }

  void add(int id, String name, String description, String imageName, AllergensHelper allergens, int calories, String currencyCode, BigDecimal cost) {
    DishHelper dish = new DishHelper(id, name, description, imageName, allergens, calories, new Money(Currency.getInstance(currencyCode), cost));
    dishes.add(dish);
  }

}
