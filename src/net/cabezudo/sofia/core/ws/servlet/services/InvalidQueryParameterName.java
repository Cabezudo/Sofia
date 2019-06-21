package net.cabezudo.sofia.core.ws.servlet.services;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.14
 */
public class InvalidQueryParameterName extends RuntimeException {

  public InvalidQueryParameterName(String parameterName) {
    super("Invalid query parameter name: " + parameterName);
  }
}
