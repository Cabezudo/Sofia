package net.cabezudo.sofia.core.users.permission;

import net.cabezudo.sofia.core.sites.SitesTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class PermissionTypesTable {

  public static final String NAME = "permissionTypes";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`name` VARCHAR(20) NOT NULL, "
          + "`site` INT NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "FOREIGN KEY (`site`) REFERENCES " + SitesTable.NAME + "(`id`), "
          + "UNIQUE INDEX `iName` (`name`, `site`)"
          + ") "
          + "CHARACTER SET = UTF8";

}
