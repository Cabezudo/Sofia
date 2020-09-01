package net.cabezudo.sofia.restaurants;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.29
 */
public class RestaurantTypesTable {

  public static final String DATABASE = "hayQueComer";
  public static final String NAME = "restaurantTypes";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`name` VARCHAR(100) NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "UNIQUE INDEX `iName` (`name`)"
          + ") "
          + "CHARACTER SET = UTF8";

  private RestaurantTypesTable() {
    // Nothing to do here. Utility classes should not have public constructors.
  }
}
