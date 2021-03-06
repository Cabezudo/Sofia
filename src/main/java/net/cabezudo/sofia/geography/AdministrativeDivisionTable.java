package net.cabezudo.sofia.geography;

import net.cabezudo.sofia.core.configuration.Configuration;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class AdministrativeDivisionTable {

  public static final String DATABASE_NAME = Configuration.getInstance().getDatabaseName();
  public static final String NAME = "administrativeDivisions";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`type` INT NOT NULL, "
          + "`code` VARCHAR(3) NOT NULL, "
          + "`fileId` INT, "
          + "`parent` INT, "
          + "PRIMARY KEY (`id`), "
          + "UNIQUE INDEX `iCode` (`code`), "
          + "INDEX `iParent` (`parent`)"
          + ") "
          + "CHARACTER SET = UTF8";

  private AdministrativeDivisionTable() {
    // Utility classes should not have public constructors.
  }
}
