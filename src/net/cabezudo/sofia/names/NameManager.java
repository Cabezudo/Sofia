package net.cabezudo.sofia.names;

import net.cabezudo.sofia.core.ws.responses.Message;
import net.cabezudo.sofia.core.ws.responses.Messages;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.16
 */
public class NameManager {

  private static NameManager INSTANCE;

  private NameManager() {
    // Nothing to do here
  }

  public static NameManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new NameManager();
    }
    return INSTANCE;
  }

  public Messages validate(String name) {
    Messages messages = new Messages();
    messages.add(new Message("name.ok"));
    return messages;
  }
}
