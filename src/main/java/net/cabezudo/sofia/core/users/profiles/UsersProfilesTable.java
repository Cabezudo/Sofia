package net.cabezudo.sofia.core.users.profiles;

import net.cabezudo.sofia.core.users.UsersTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class UsersProfilesTable {

  public static final String NAME = "usersProfiles";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`user` INT NOT NULL, "
          + "`profile` INT NOT NULL, "
          + "`owner` INT NOT NULL, "
          + "PRIMARY KEY (`user`, `profile`, `owner`), "
          + "FOREIGN KEY (`user`) REFERENCES " + UsersTable.NAME + "(`id`), "
          + "FOREIGN KEY (`profile`) REFERENCES " + ProfilesTable.NAME + "(`id`), "
          + "FOREIGN KEY (`owner`) REFERENCES " + UsersTable.NAME + "(`id`)"
          + ") "
          + "CHARACTER SET = UTF8";

  private UsersProfilesTable() {
    // Nothing to do here. Utility classes should not have public constructors.
  }
}
