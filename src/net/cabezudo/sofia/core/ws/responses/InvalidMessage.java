package net.cabezudo.sofia.core.ws.responses;

import static net.cabezudo.sofia.core.ws.responses.AbstractMessage.Type.INVALID;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.10
 */
public class InvalidMessage extends AbstractMessage {

  public InvalidMessage(String message, Object... os) {
    super(INVALID, message, os);
  }

}
