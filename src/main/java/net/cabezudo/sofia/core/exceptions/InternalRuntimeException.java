package net.cabezudo.sofia.core.exceptions;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.07.16
 */
public class InternalRuntimeException extends RuntimeException {

  public InternalRuntimeException(String message) {
    super(message);
  }

  public InternalRuntimeException(Throwable cause) {
    super(cause);
  }

  public InternalRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

}
