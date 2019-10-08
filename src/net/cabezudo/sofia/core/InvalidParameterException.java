package net.cabezudo.sofia.core;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.05
 */
public class InvalidParameterException extends RuntimeException {

  public InvalidParameterException(String message) {
    super(message);
  }
}
