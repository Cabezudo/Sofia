package net.cabezudo.sofia.core.webusers;

import net.cabezudo.sofia.core.languages.LanguagesTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.04
 */
public class WebUserDataTable {

  public static final String DATABASE_NAME = "sofia";
  public static final String NAME = "webUsersData";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`sessionId` VARCHAR(32) NOT NULL, "
          + "`failLoginResponseTime` INT NOT NULL, "
          + "`countryLanguage` INT NOT NULL, "
          + "`actualLanguage` INT NOT NULL, "
          + "`user` INT NOT NULL DEFAULT 0, "
          + "PRIMARY KEY (`id`), "
          + "UNIQUE INDEX `iSessionId` (`sessionId`(32)), "
          + "FOREIGN KEY (`countryLanguage`) REFERENCES " + LanguagesTable.DATABASE_NAME + "." + LanguagesTable.NAME + "(`id`), "
          + "FOREIGN KEY (`actualLanguage`) REFERENCES " + LanguagesTable.DATABASE_NAME + "." + LanguagesTable.NAME + "(`id`)"
          + ") "
          + "CHARACTER SET=UTF8";

  private WebUserDataTable() {
    // Nothing to do here. Utility classes should not have public constructors.
  }
}
