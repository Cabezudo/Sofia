package net.cabezudo.sofia.core.times;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.17
 */
public class TimeEntriesTable {

  private TimeEntriesTable() {
    // Nothing to do here
  }
  public static final String DATABASE = "sofia";
  public static final String NAME = "timeEntries";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "PRIMARY KEY (`id`)"
          + ") "
          + "CHARACTER SET = UTF8";
}
