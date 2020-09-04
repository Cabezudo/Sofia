package net.cabezudo.sofia.food;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class CategoriesTable {

  public static final String DATABASE = "hayQueComer";
  public static final String NAME = "categories";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`restaurant` INT NOT NULL, "
          + "`name` VARCHAR(50) NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "INDEX `iRestaurant` (`restaurant`), "
          + "UNIQUE INDEX `iRestaurantName` (`restaurant`, `name`)"
          + ") "
          + "CHARACTER SET = UTF8";

  private CategoriesTable() {
    // Nothing to do here. Utility classes should not have public constructors.
  }
}
