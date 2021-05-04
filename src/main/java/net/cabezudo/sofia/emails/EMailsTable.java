package net.cabezudo.sofia.emails;

import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.people.PeopleTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.17
 */
public class EMailsTable {

  private EMailsTable() {
    // Nothing to do here
  }

  public static final String DATABASE_NAME = Configuration.getInstance().getDatabaseName();
  public static final String NAME = "emails";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + DATABASE_NAME + "." + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`personId` INT NOT NULL, "
          + "`address` VARCHAR(" + EMail.MAX_LENGTH + ") NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "FOREIGN KEY (`personId`) REFERENCES " + PeopleTable.DATABASE_NAME + "." + PeopleTable.NAME + "(`id`), "
          + "INDEX `iPersonId` (`personId`), "
          + "UNIQUE INDEX `iAddress` (`address`)"
          + ") "
          + "CHARACTER SET = UTF8";
}
