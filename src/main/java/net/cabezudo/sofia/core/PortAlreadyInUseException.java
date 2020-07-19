package net.cabezudo.sofia.core;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.06.13
 */
public class PortAlreadyInUseException extends Exception {

  public PortAlreadyInUseException(String message) {
    super(message);
  }

}
