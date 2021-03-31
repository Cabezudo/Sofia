package net.cabezudo.sofia.core.currency;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.11.25
 */
public class InvalidCurrencyCodeException extends Exception {

  public InvalidCurrencyCodeException(String twoLettersCode) {
    super("Invalid currency code: " + twoLettersCode);
  }

}
