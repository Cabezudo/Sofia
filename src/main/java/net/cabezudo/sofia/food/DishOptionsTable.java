package net.cabezudo.sofia.food;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class DishOptionsTable {

  public static final String DATABASE = "hayQueComer";
  public static final String NAME = "dishOptions";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`group` INT NOT NULL, "
          + "`order` INT(2) NOT NULL, "
          + "`name` VARCHAR(50) NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "FOREIGN KEY (`group`) REFERENCES " + DishOptionsGroupsTable.DATABASE + "." + DishOptionsGroupsTable.NAME + "(`id`), "
          + "INDEX `iGroupOrder` (`group`, `order`)"
          + ") "
          + "CHARACTER SET = UTF8";

  private DishOptionsTable() {
    // Nothing to do here. Utility classes should not have public constructors.
  }
}
