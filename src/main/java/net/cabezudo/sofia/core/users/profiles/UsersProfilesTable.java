package net.cabezudo.sofia.core.users.profiles;

import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.users.UsersTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class UsersProfilesTable {

  public static final String DATABASE_NAME = Configuration.getInstance().getDatabaseName();
  public static final String NAME = "usersProfiles";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + DATABASE_NAME + "." + NAME + " "
          + "("
          + "`user` INT NOT NULL, "
          + "`profile` INT NOT NULL, "
          + "`owner` INT NOT NULL, "
          + "PRIMARY KEY (`user`, `profile`, `owner`), "
          + "FOREIGN KEY (`user`) REFERENCES " + UsersTable.DATABASE_NAME + "." + UsersTable.NAME + "(`id`), "
          + "FOREIGN KEY (`profile`) REFERENCES " + ProfilesTable.DATABASE_NAME + "." + ProfilesTable.NAME + "(`id`), "
          + "FOREIGN KEY (`owner`) REFERENCES " + UsersTable.DATABASE_NAME + "." + UsersTable.NAME + "(`id`)"
          + ") "
          + "CHARACTER SET = UTF8";

  private UsersProfilesTable() {
    // Utility classes should not have public constructors.
  }
}
