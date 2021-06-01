package net.cabezudo.sofia.core.configuration;

import net.cabezudo.sofia.core.PositionFile;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.19
 */
public class ConfigurationException extends Exception {

  private final PositionFile positionFile;

  public ConfigurationException(String message, PositionFile positionFile, Throwable cause) {
    super(message, cause);
    this.positionFile = positionFile;
  }

  public ConfigurationException(String message, PositionFile positionFile) {
    super(message);
    this.positionFile = positionFile;
  }

  public ConfigurationException(PositionFile positionFile, Throwable cause) {
    super(cause);
    this.positionFile = positionFile;
  }

  public ConfigurationException(String message, Throwable cause) {
    this(message, null, cause);
  }

  public ConfigurationException(String message) {
    this(message, (PositionFile) null);
  }

  public ConfigurationException(Throwable cause) {
    this((PositionFile) null, cause);
  }

  public PositionFile getPositionFile() {
    return positionFile;
  }
}
