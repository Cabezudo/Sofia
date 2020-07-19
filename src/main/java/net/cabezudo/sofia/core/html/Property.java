package net.cabezudo.sofia.core.html;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.05.14
 */
class Property {

  private final String value;
  private final int column;

  Property(String value, int column) {
    this.value = value;
    this.column = column;
  }

  String getValue() {
    return value;
  }

  int getColumn() {
    return column;
  }

}
