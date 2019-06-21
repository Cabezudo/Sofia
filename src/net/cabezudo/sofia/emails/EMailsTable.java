package net.cabezudo.sofia.emails;

import net.cabezudo.sofia.core.database.Table;
import net.cabezudo.sofia.people.PeopleTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.17
 */
public class EMailsTable extends Table {

  public static final String NAME = "emails";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`personId` INT NOT NULL, "
          + "`address` VARCHAR(" + EMail.MAX_LENGTH + ") NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "FOREIGN KEY (`personId`) REFERENCES " + PeopleTable.NAME + "(`id`), "
          + "INDEX `iPersonId` (`personId`), "
          + "UNIQUE INDEX `iAddress` (`address`)"
          + ") "
          + "CHARACTER SET = UTF8";
}
