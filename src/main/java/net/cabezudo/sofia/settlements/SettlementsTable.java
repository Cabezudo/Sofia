package net.cabezudo.sofia.settlements;

import net.cabezudo.sofia.core.users.UsersTable;
import net.cabezudo.sofia.municipalities.MunicipalitiesTable;
import net.cabezudo.sofia.zones.ZonesTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class SettlementsTable {

  public static final String NAME = "settlements";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`type` INT NOT NULL, "
          + "`city` INT, "
          + "`municipality` INT NOT NULL, "
          + "`zone` INT NOT NULL, "
          + "`name` VARCHAR(100) NOT NULL, "
          + "`owner` INT NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "FOREIGN KEY (`owner`) REFERENCES " + UsersTable.NAME + "(`id`), "
          + "FOREIGN KEY (`type`) REFERENCES " + SettlementTypesTable.NAME + "(`id`), "
          + "FOREIGN KEY (`municipality`) REFERENCES " + MunicipalitiesTable.NAME + "(`id`), "
          + "FOREIGN KEY (`zone`) REFERENCES " + ZonesTable.NAME + "(`id`), "
          + "UNIQUE INDEX `iTypeMunicipalityZoneName` (`type`, `municipality`, `zone`, `name`)"
          + ") "
          + "CHARACTER SET = UTF8";

  private SettlementsTable() {
    // Nothing to do here. Utility classes should not have public constructors.
  }
}
