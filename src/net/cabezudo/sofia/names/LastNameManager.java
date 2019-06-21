package net.cabezudo.sofia.names;

import net.cabezudo.sofia.core.ws.responses.Message;
import net.cabezudo.sofia.core.ws.responses.Messages;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.16
 */
public class LastNameManager {

  private static LastNameManager INSTANCE;

  private LastNameManager() {
    // Nothing to do here
  }

  public static LastNameManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new LastNameManager();
    }
    return INSTANCE;
  }

  public Messages validate(String lastName) {
    Messages messages = new Messages();
    messages.add(new Message("lastName.ok"));
    return messages;
  }
}
