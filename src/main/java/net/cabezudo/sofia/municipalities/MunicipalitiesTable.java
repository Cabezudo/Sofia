package net.cabezudo.sofia.municipalities;

import net.cabezudo.sofia.core.users.UsersTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class MunicipalitiesTable {

  public static final String NAME = "municipalities";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`state` INT NOT NULL, "
          + "`name` VARCHAR(100) NOT NULL, "
          + "`owner` INT NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "FOREIGN KEY (`owner`) REFERENCES " + UsersTable.NAME + "(`id`), "
          + "UNIQUE INDEX `iStateName` (`state`, `name`)"
          + ") "
          + "CHARACTER SET = UTF8";

  private MunicipalitiesTable() {
    // Nothing to do here. Utility classes should not have public constructors.
  }
}
