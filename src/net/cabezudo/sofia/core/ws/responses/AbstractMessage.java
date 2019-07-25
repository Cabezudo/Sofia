package net.cabezudo.sofia.core.ws.responses;

import java.util.Locale;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.texts.TextManager;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.10
 */
public abstract class AbstractMessage {

  private final String message;
  private final Type type;
  private final Object[] os;

  protected enum Type {
    OK, VALID, ERROR, INVALID, WARNING, NO_LOGGED, SERVICE_UNAVAILABLE, INTERNAL_SERVER_ERROR;
  }

  protected AbstractMessage(Type type, String message, Object... os) {
    this.type = type;
    this.message = message;
    this.os = os;
  }

  public Type getType() {
    return type;
  }

  public JSONObject toJSON(Site site, Locale locale) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.add(new JSONPair("status", type.toString()));
    jsonObject.add(new JSONPair("message", TextManager.get(site, locale, message, os)));
    return jsonObject;
  }
}
