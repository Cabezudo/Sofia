package net.cabezudo.sofia.geography;

import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.languages.LanguagesTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class AdministrativeDivisionNameTable {

  public static final String DATABASE_NAME = Configuration.getInstance().getDatabaseName();
  public static final String NAME = "administrativeDivisionNames";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL, "
          + "`language` INT NOT NULL, "
          + "`value` VARCHAR(100) NOT NULL, "
          + "FOREIGN KEY (`language`) REFERENCES " + LanguagesTable.DATABASE_NAME + "." + LanguagesTable.NAME + "(`id`), "
          + "UNIQUE INDEX `iIdLanguage` (`id`, `language`)"
          + ") "
          + "CHARACTER SET = UTF8";

  private AdministrativeDivisionNameTable() {
    // Utility classes should not have public constructors.
  }
}
