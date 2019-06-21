package net.cabezudo.sofia.countries;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class CountriesTable {

  public static final String NAME = "countries";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + CountriesTable.NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`name` VARCHAR(100) NOT NULL, "
          + "`phoneCode` INT NOT NULL, "
          + "`twoLettersCountryCode` CHAR(2) NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "UNIQUE INDEX `iName` (`name`)"
          + ") "
          + "CHARACTER SET = UTF8";
}
