package net.cabezudo.sofia.core.configuration;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.10.10
 */
public class DataCreationException extends Exception {

  public DataCreationException(Throwable cause) {
    super(cause);
  }

  public DataCreationException(String message, Throwable cause) {
    super(message, cause);
  }
}
