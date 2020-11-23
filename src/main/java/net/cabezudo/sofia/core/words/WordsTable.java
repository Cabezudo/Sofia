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

  public static final String DATABASE = "sofia";
  public static final String NAME = "words";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`language` INT NOT NULL, "
          + "`value` VARCHAR(100) NOT NULL, "
          + "PRIMARY KEY (`id`, `language`), "
          + "FOREIGN KEY (`language`) REFERENCES " + LanguagesTable.DATABASE + "." + LanguagesTable.NAME + "(`id`)"
          + ") "
          + "CHARACTER SET = UTF8";

}
