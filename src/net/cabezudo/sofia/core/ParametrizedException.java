package net.cabezudo.sofia.core;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.09.27
 */
public class ParametrizedException extends Exception {

  private final Object[] parameters;

  public ParametrizedException(String messageKey, Object... parameters) {
    super(messageKey);
    this.parameters = parameters;
  }

  public Object[] getParameters() {
    return parameters;
  }
}
