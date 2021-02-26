package net.cabezudo.sofia.core.database;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.02.24
 */
public class ValidSortColumns {

  private final String[] columns;

  public ValidSortColumns(String... columnNames) {
    this.columns = columnNames;
  }

  public String[] toArray() {
    return columns;
  }

}
