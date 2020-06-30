package net.cabezudo.sofia.core.sic;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.sic.tokens.Position;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.25
 */
public class SICCompilerMessage {

  private final String message;
  private final Position position;

  public SICCompilerMessage(String message, Position position) {
    this.message = message;
    this.position = position;
  }

  public JSONObject toJSON() {
    JSONObject jsonMessage = new JSONObject();
    JSONPair jsonPairMessage = new JSONPair("message", message);
    jsonMessage.add(jsonPairMessage);
    JSONPair jsonPairPosition = new JSONPair("position", position.toJSON());
    jsonMessage.add(jsonPairPosition);
    return jsonMessage;
  }
}
