package net.cabezudo.sofia.core.ws;

import java.util.ArrayList;
import java.util.List;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.core.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.02.26
 */
class WebServicesMethods {

  private List<WebServiceMethod> methods = new ArrayList<>();

  void add(JSONArray jsonArray) throws PropertyNotExistException {
    if (jsonArray == null) {
      return;
    }
    for (JSONValue jsonValue : jsonArray) {
      JSONObject jsonObject = jsonValue.toJSONObject();
      String url = jsonObject.getString("url");
      String className = jsonObject.getString("class");
      if (className.isEmpty()) {
        Logger.debug("Skip: %s", url);
      } else {
        WebServiceMethod webServiceMethod = new WebServiceMethod(url, className);
        methods.add(webServiceMethod);
        Logger.debug("Add: %s", webServiceMethod.toString());
      }
    }
  }

}
