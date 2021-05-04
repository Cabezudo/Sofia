package net.cabezudo.sofia.addresses;

import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.users.UsersTable;
import net.cabezudo.sofia.postalcodes.PostalCodesTable;
import net.cabezudo.sofia.streets.StreetsTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class AddressesTable {

  private AddressesTable() {
    // Utility classes should not have public constructors.
  }

  public static final String DATABASE_NAME = Configuration.getInstance().getDatabaseName();
  public static final String NAME = "addresses";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + DATABASE_NAME + "." + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`street` INT NOT NULL, "
          + "`exteriorNumber` VARCHAR(10) NOT NULL, "
          + "`interiorNumber` VARCHAR(10), "
          + "`postalCode` INT NOT NULL, "
          + "`owner` INT NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "FOREIGN KEY (`street`) REFERENCES " + StreetsTable.DATABASE_NAME + "." + StreetsTable.NAME + "(`id`), "
          + "FOREIGN KEY (`postalCode`) REFERENCES " + PostalCodesTable.DATABASE_NAME + "." + PostalCodesTable.NAME + "(`id`), "
          + "FOREIGN KEY (`owner`) REFERENCES " + UsersTable.DATABASE_NAME + "." + UsersTable.NAME + "(`id`), "
          + "UNIQUE INDEX `iAllData` (`street`, `exteriorNumber`, `interiorNumber`, `postalCode`, `owner`), "
          + "INDEX `iOwner` (`owner`)"
          + ") "
          + "CHARACTER SET = UTF8";

}
