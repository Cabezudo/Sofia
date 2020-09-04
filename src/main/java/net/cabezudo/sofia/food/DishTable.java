package net.cabezudo.sofia.food;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class DishTable {

  public static final String DATABASE = "hayQueComer";
  public static final String NAME = "dishes";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`dishGroup` INT NOT NULL, "
          + "`name` VARCHAR(200) NOT NULL, "
          + "`description` VARCHAR(400), "
          + "`imageName` VARCHAR(50), "
          + "`calories` INT(5), "
          + "`currencyCode` VARCHAR(3) NOT NULL, "
          + "`cost` DECIMAL(8,2) NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "FOREIGN KEY (`dishGroup`) REFERENCES " + DishGroupsTable.DATABASE + "." + DishGroupsTable.NAME + "(`id`)"
          + ") "
          + "CHARACTER SET = UTF8";

  private DishTable() {
    // Nothing to do here. Utility classes should not have public constructors.
  }
}
