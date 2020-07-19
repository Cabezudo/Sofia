package net.cabezudo.sofia.core.configuration;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2010.07.18
 */
public class RuntimeConfigurationException extends RuntimeException {

  public RuntimeConfigurationException(Throwable cause) {
    super(cause);
  }

  public RuntimeConfigurationException(String message) {
    super(message);
  }

  public RuntimeConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}
