package net.cabezudo.sofia.core;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.05
 */
public class InvalidPathParameterException extends Exception {

  public InvalidPathParameterException(String message) {
    super(message);
  }

  public InvalidPathParameterException(Throwable cause) {
    super(cause);
  }
}
