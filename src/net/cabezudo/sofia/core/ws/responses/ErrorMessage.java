package net.cabezudo.sofia.core.ws.responses;

import static net.cabezudo.sofia.core.ws.responses.AbstractMessage.Type.ERROR;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.10
 */
public class ErrorMessage extends AbstractMessage {

  public ErrorMessage(String message, Object... os) {
    super(ERROR, message, os);
  }
}
