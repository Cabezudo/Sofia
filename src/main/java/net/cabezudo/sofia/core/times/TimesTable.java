package net.cabezudo.sofia.core.times;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.17
 */
public class TimesTable {

  private TimesTable() {
    // Nothing to do here
  }
  public static final String DATABASE = "sofia";
  public static final String NAME = "times";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`entry` INT NOT NULL, "
          + "`start` INT NOT NULL, "
          + "`end` INT NOT NULL, "
          + "`type` INT NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "UNIQUE INDEX `iName` (`id`), "
          + "FOREIGN KEY (`entry`) REFERENCES " + TimeEntriesTable.NAME + "(`id`), "
          + "FOREIGN KEY (`type`) REFERENCES " + TimeTypesTable.NAME + "(`id`)"
          + ") "
          + "CHARACTER SET = UTF8";
}
