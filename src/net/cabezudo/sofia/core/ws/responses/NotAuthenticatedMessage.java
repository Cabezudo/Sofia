package net.cabezudo.sofia.core.ws.responses;

import static net.cabezudo.sofia.core.ws.responses.AbstractMessage.Type.NO_LOGGED;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.10
 */
public class NotAuthenticatedMessage extends AbstractMessage {

  public NotAuthenticatedMessage(String message, Object... os) {
    super(NO_LOGGED, message, os);
  }

}
