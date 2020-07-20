package net.cabezudo.sofia.core;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.09.27
 */
// TODO quitar esto y hacer cada excepción propia del error para poder pasar los parámetros y que quede mas clara
public class ParametrizedException extends Exception {

  private final String[] parameters;

  public ParametrizedException(String messageKey, String... parameters) {
    super(messageKey);
    this.parameters = parameters;
  }

  public String[] getParameters() {
    return parameters;
  }
}
