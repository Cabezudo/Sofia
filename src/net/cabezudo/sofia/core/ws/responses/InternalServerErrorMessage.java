package net.cabezudo.sofia.core.ws.responses;

import static net.cabezudo.sofia.core.ws.responses.AbstractMessage.Type.INTERNAL_SERVER_ERROR;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.10
 */
public class InternalServerErrorMessage extends AbstractMessage {

  public InternalServerErrorMessage(String message, Object... os) {
    super(INTERNAL_SERVER_ERROR, message, os);
  }
}
