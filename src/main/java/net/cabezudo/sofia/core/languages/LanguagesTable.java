package net.cabezudo.sofia.core.languages;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.18
 */
public class LanguagesTable {

  public static final String DATABASE = "sofia";
  public static final String NAME = "languages";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`twoLettersCode` VARCHAR(2) NOT NULL, "
          + "PRIMARY KEY (`id`)"
          + ") "
          + "CHARACTER SET = UTF8";

  private LanguagesTable() {
    // Nothing to do here. Utility classes should not have public constructors.
  }
}