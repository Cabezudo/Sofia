package net.cabezudo.sofia.core.ws.responses;

import java.util.Locale;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.04
 */
public class MultipleMessageResponse extends AbstractResponse {

  private final String type;
  private final Messages messages;

  public MultipleMessageResponse(String type) {
    this.type = type;
    this.messages = new Messages();
  }

  public MultipleMessageResponse(String type, Messages messages) {
    this.type = type;
    this.messages = messages;
  }

  @Override
  public String toJSON(Site site, Locale locale) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.add(new JSONPair("type", type));
    jsonObject.add(new JSONPair("messages", messages.toJSON(site, locale)));
    return jsonObject.toString();
  }
}
