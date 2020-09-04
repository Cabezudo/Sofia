package net.cabezudo.sofia.food;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class DishGroupsTable {

  public static final String DATABASE = "hayQueComer";
  public static final String NAME = "dishGroups";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`category` INT NOT NULL, "
          + "`name` VARCHAR(50) NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "FOREIGN KEY (`category`) REFERENCES " + CategoriesTable.DATABASE + "." + CategoriesTable.NAME + "(`id`), "
          + "INDEX `iCategory` (`category`)"
          + ") "
          + "CHARACTER SET = UTF8";

  private DishGroupsTable() {
    // Nothing to do here. Utility classes should not have public constructors.
  }
}
