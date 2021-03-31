package net.cabezudo.sofia.core.currency;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.03.30
 */
public class CurrenciesTable {

  public static final String DATABASE_NAME = "sofia";
  public static final String NAME = "currencies";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`currencyCode` VARCHAR(3) NOT NULL, "
          + "PRIMARY KEY (`id`)"
          + ") "
          + "CHARACTER SET = UTF8";

  private CurrenciesTable() {
    // Utility classes should not have public constructors
  }
}
