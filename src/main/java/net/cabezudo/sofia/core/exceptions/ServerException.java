package net.cabezudo.sofia.core.exceptions;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.07.18
 */
public class ServerException extends Exception {

  public ServerException(Throwable cause) {
    super("Server error.", cause);
  }

  public ServerException(String message, Throwable cause) {
    super(message, cause);
  }
}
