package net.cabezudo.sofia.core.webusers;

import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.currency.CurrenciesTable;
import net.cabezudo.sofia.core.languages.LanguagesTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.04
 */
public class WebUserDataTable {

  public static final String DATABASE_NAME = Configuration.getInstance().getDatabaseName();
  public static final String NAME = "webUsersData";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`sessionId` VARCHAR(80) NOT NULL, "
          + "`failLoginResponseTime` INT NOT NULL, "
          + "`countryLanguage` INT NOT NULL, "
          + "`actualLanguage` INT NOT NULL, "
          + "`actualCurrency` INT NOT NULL, "
          + "`user` INT NOT NULL DEFAULT 0, "
          + "PRIMARY KEY (`id`), "
          + "UNIQUE INDEX `iSessionId` (`sessionId`(32)), "
          + "FOREIGN KEY (`countryLanguage`) REFERENCES " + LanguagesTable.DATABASE_NAME + "." + LanguagesTable.NAME + "(`id`), "
          + "FOREIGN KEY (`actualLanguage`) REFERENCES " + LanguagesTable.DATABASE_NAME + "." + LanguagesTable.NAME + "(`id`),"
          + "FOREIGN KEY (`actualCurrency`) REFERENCES " + CurrenciesTable.DATABASE_NAME + "." + CurrenciesTable.NAME + "(`id`)"
          + ") "
          + "CHARACTER SET=UTF8";

  private WebUserDataTable() {
    // Utility classes should not have public constructors.
  }
}
