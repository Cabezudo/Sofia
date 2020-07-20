package net.cabezudo.sofia.core.exceptions;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.07.16
 */
public class SofiaRuntimeException extends RuntimeException {

  public SofiaRuntimeException(String message) {
    super(message);
  }

  public SofiaRuntimeException(Throwable cause) {
    super(cause);
  }

  public SofiaRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

}
