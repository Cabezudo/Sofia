package net.cabezudo.sofia.core.ws.responses;

import net.cabezudo.sofia.core.ParametrizedException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.10
 */
public class ValidationResponse extends Response {

  public ValidationResponse(Status status, String message, String... os) {
    super(status, Response.Type.VALIDATION, message, os);
  }

  public ValidationResponse(Status status, ParametrizedException e) {
    this(status, e.getMessage(), e.getParameters());
  }
}
