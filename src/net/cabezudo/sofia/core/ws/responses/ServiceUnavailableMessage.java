package net.cabezudo.sofia.core.ws.responses;

import static net.cabezudo.sofia.core.ws.responses.AbstractMessage.Type.SERVICE_UNAVAILABLE;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.10
 */
public class ServiceUnavailableMessage extends AbstractMessage {

  public ServiceUnavailableMessage(String message, Object... os) {
    super(SERVICE_UNAVAILABLE, message, os);
  }
}
