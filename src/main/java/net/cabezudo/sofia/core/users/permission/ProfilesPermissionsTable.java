package net.cabezudo.sofia.core.users.permission;

import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.sites.SitesTable;
import net.cabezudo.sofia.core.users.profiles.ProfilesTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class ProfilesPermissionsTable {

  public static final String DATABASE_NAME = Configuration.getInstance().getDatabaseName();
  public static final String NAME = "profilesPermissions";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + DATABASE_NAME + "." + NAME + " "
          + "("
          + "`profile` INT NOT NULL, "
          + "`permission` INT NOT NULL, "
          + "`site` INT NOT NULL, "
          + "PRIMARY KEY (`profile`, `permission`, `site`), "
          + "FOREIGN KEY (`profile`) REFERENCES " + ProfilesTable.DATABASE_NAME + "." + ProfilesTable.NAME + "(`id`), "
          + "FOREIGN KEY (`permission`) REFERENCES " + PermissionsTable.DATABASE_NAME + "." + PermissionsTable.NAME + "(`id`), "
          + "FOREIGN KEY (`site`) REFERENCES " + SitesTable.DATABASE_NAME + "." + SitesTable.NAME + "(`id`)"
          + ") "
          + "CHARACTER SET = UTF8";

  private ProfilesPermissionsTable() {
    // Utility classes should not have public constructors.
  }
}
