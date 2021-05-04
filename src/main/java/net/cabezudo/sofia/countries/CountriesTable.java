package net.cabezudo.sofia.countries;

import net.cabezudo.sofia.core.configuration.Configuration;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class CountriesTable {

  public static final String DATABASE_NAME = Configuration.getInstance().getDatabaseName();
  public static final String NAME = "countries";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + DATABASE_NAME + "." + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`twoLettersCountryCode` CHAR(2) NOT NULL, "
          + "`phoneCode` INT NOT NULL, "
          + "PRIMARY KEY (`id`)"
          + ") "
          + "CHARACTER SET = UTF8";

  private CountriesTable() {
    // Utility classes should not have public constructors.
  }
}
