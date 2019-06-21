package net.cabezudo.sofia.core.ws.responses;

import static net.cabezudo.sofia.core.ws.responses.AbstractMessage.Type.VALID;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.10
 */
public class ValidMessage extends AbstractMessage {

  public ValidMessage(String message, Object... os) {
    super(VALID, message, os);
  }

}
