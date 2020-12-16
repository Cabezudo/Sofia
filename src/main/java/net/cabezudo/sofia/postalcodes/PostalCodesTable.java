package net.cabezudo.sofia.postalcodes;

import net.cabezudo.sofia.core.users.UsersTable;
import net.cabezudo.sofia.settlements.SettlementsTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class PostalCodesTable {

  private PostalCodesTable() {
    // Nothing to do here
  }

  public static final String NAME = "postalCodes";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`settlement` INT NOT NULL, "
          + "`postalCode` INT NOT NULL, "
          + "`owner` INT NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "FOREIGN KEY (`settlement`) REFERENCES " + SettlementsTable.NAME + "(`id`), "
          + "FOREIGN KEY (`owner`) REFERENCES " + UsersTable.NAME + "(`id`), "
          + "UNIQUE INDEX `iSettlementPostalCode` (`settlement`, `postalCode`)"
          + ") "
          + "CHARACTER SET = UTF8";

}

// Numero de cuenta total play: 0105320307

