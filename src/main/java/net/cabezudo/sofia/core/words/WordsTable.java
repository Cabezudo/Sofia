package net.cabezudo.sofia.core.words;

import net.cabezudo.sofia.core.languages.LanguagesTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class WordsTable {

  private WordsTable() {
    // Nothing to do here. Utility classes should not have public constructors.
  }

  public static String getCreationQuery(String targetDatabaseName, String targetTableName, String databaseName, String tableName) {
    return "CREATE TABLE " + databaseName + "." + tableName + " "
            + "("
            + "`id` INT NOT NULL, "
            + "`language` INT NOT NULL, "
            + "`value` VARCHAR(1000), "
            + "PRIMARY KEY (`id`, `language`), "
            + "FOREIGN KEY (`id`) REFERENCES " + targetDatabaseName + "." + targetTableName + "(`id`), "
            + "FOREIGN KEY (`language`) REFERENCES " + LanguagesTable.DATABASE_NAME + "." + LanguagesTable.NAME + "(`id`)"
            + ") "
            + "CHARACTER SET = UTF8";
  }
}
