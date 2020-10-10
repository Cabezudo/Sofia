package net.cabezudo.sofia.food;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class DishOptionsGroupsTable {

  public static final String DATABASE = "hayQueComer";
  public static final String NAME = "dishOptionsGroups";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`dish` INT NOT NULL, "
          + "`order` INT(2) NOT NULL, "
          + "`name` VARCHAR(50) NOT NULL, "
          + "`type` INT(2) NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "FOREIGN KEY (`dish`) REFERENCES " + DishTable.DATABASE + "." + DishTable.NAME + "(`id`), "
          + "INDEX `iDishOrder` (`dish`, `order`)"
          + ") "
          + "CHARACTER SET = UTF8";

  private DishOptionsGroupsTable() {
    // Nothing to do here. Utility classes should not have public constructors.
  }
}
