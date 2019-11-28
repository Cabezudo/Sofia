package net.cabezudo.sofia.core.creator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.25
 */
class CSSFactory {

  static Line get(String line, int lineNumber, Caller caller) {
    return new CodeLine(line, lineNumber);
  }

}
