package net.cabezudo.sofia.core.users.profiles;

import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.sites.SitesTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class ProfilesTable {

  public static final String DATABASE_NAME = Configuration.getInstance().getDatabaseName();
  public static final String NAME = "profiles";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + DATABASE_NAME + "." + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`name` VARCHAR(20) NOT NULL, "
          + "`site` INT NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "FOREIGN KEY (`site`) REFERENCES " + SitesTable.DATABASE_NAME + "." + SitesTable.NAME + "(`id`), "
          + "UNIQUE INDEX `iName` (`name`, `site`)"
          + ") "
          + "CHARACTER SET = UTF8";

  private ProfilesTable() {
    // Utility classes should not have public constructors.
  }
}
