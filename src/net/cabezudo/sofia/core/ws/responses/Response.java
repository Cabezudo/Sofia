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
public class Response {

  private JSONObject jsonObject;
  private String id;
  private String message;
  private Object[] os;

  public Response(String id, String message, Object... os) {
    this.id = id;
    this.message = message;
    this.os = os;
  }

  public Response(String ok, JSONObject jsonObject) {
    this.jsonObject = jsonObject;
  }

  public JSONObject toJSON(Site site, Locale locale) {
    if (jsonObject == null) {
      jsonObject = new JSONObject();
      jsonObject.add(new JSONPair("status", id));
      jsonObject.add(new JSONPair("message", TextManager.get(site, locale, message, os)));
    }
    return jsonObject;
  }
}
