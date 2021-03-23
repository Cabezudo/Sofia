package net.cabezudo.sofia.core.ws.responses;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.sites.texts.TextManager;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.10
 */
public class Response {

  public enum Type {
    ACTION, CREATE, DATA, READ, SET, UPDATE, DELETE, VALIDATION, NOT_FOUND, INVALID
  }

  public enum Status {
    OK, WARNING, ERROR, FAIL, NOT_LOGGED, LOGGED
  }

  private JSONObject jsonObject;
  private final Status status;
  private final Type messageType;
  private final JSONObject data;
  private final String message;
  private final Object[] os;

  public Response(Status status, Type messageType, String message, String... os) {
    this(status, messageType, null, message, os);
  }

  public Response(Status status, Type messageType, JSONObject data, String message, String... os) {
    this.status = status;
    this.messageType = messageType;
    this.data = data;
    this.message = message;
    this.os = os;
  }

  public JSONObject toJSON(Language language) {
    if (jsonObject == null) {
      jsonObject = new JSONObject();
      jsonObject.add(new JSONPair("status", status.toString()));
      jsonObject.add(new JSONPair("type", messageType.toString()));
      jsonObject.add(new JSONPair("message", TextManager.get(language, message, os)));
      if (data != null) {
        jsonObject.add(new JSONPair("data", data));
      }
    }
    return jsonObject;
  }
}
