package net.cabezudo.sofia.settlements;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.29
 */
public class SettlementTypesTable {

  public static final String NAME = "settlementTypes";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`name` VARCHAR(100) NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "UNIQUE INDEX `iName` (`name`)"
          + ") "
          + "CHARACTER SET = UTF8";

}
