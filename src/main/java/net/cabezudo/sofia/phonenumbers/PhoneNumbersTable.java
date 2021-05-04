package net.cabezudo.sofia.phonenumbers;

import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.people.PeopleTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.17
 */
public class PhoneNumbersTable {

  public static final String DATABASE_NAME = Configuration.getInstance().getDatabaseName();
  public static final String NAME = "phoneNumbers";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`personId` INT NOT NULL, "
          + "`number` INT(" + PhoneNumber.MAX_LENGTH + ") NOT NULL, "
          + "PRIMARY KEY (`personId`), "
          + "FOREIGN KEY (`personId`) REFERENCES " + PeopleTable.DATABASE_NAME + "." + PeopleTable.NAME + "(`id`)"
          + ") "
          + "CHARACTER SET = UTF8";

  private PhoneNumbersTable() {
    // Utility classes should not have public constructors.
  }
}
