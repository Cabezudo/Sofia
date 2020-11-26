package net.cabezudo.sofia.core.languages;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.11.25
 */
public class InvalidTwoLettersCodeException extends Exception {

  public InvalidTwoLettersCodeException(String twoLettersCode) {
    super("Invalid two letter code: " + twoLettersCode);
  }

}
