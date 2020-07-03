package net.cabezudo.sofia.core.sic;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.sic.tokens.Position;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.07.03
 */
public class Message {

  private final String message;
  private final Position position;

  Message(String message, Position position) {
    this.message = message;
    this.position = position;
  }

  Message(String message) {
    this(message, null);
  }

  public JSONObject toJSON() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.add(new JSONPair("message", message));
    if (position != null) {
      jsonObject.add(new JSONPair("position", position.toJSON()));
    }
    return jsonObject;
  }

}
