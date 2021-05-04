package net.cabezudo.sofia.core.schedule;

import net.cabezudo.sofia.core.configuration.Configuration;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.17
 */
public class TimeEntriesTable {

  private TimeEntriesTable() {
    // Utility classes should not have public constructors
  }
  public static final String DATABASE_NAME = Configuration.getInstance().getDatabaseName();
  public static final String NAME = "timeEntries";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "PRIMARY KEY (`id`)"
          + ") "
          + "CHARACTER SET = UTF8";
}
