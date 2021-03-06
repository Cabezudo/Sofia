package net.cabezudo.sofia.streets;

import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.users.UsersTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class StreetsTable {

  private StreetsTable() {
    // Utility classes should not have public constructors.
  }

  public static final String DATABASE_NAME = Configuration.getInstance().getDatabaseName();
  public static final String NAME = "streets";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`name` INT NOT NULL, "
          + "`owner` INT NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "FOREIGN KEY (`owner`) REFERENCES " + UsersTable.DATABASE_NAME + "." + UsersTable.NAME + "(`id`), "
          + "UNIQUE INDEX `iNameOwner` (`name`, `owner`)"
          + ") "
          + "CHARACTER SET = UTF8";

}
