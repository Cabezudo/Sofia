package net.cabezudo.sofia.people;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.17
 */
public class PeopleTable {

  private PeopleTable() {
    // Nothing to do here
  }

  public static final String NAME = "people";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`name` VARCHAR(60) DEFAULT NULL, "
          + "`lastName` VARCHAR(60) DEFAULT NULL, "
          + "`primaryEMailId` INT, "
          + "`owner` INT NOT NULL, "
          + "PRIMARY KEY (`id`)"
          + ") "
          + "CHARACTER SET=UTF8";
}
