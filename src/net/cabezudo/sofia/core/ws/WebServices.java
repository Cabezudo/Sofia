package net.cabezudo.sofia.core.ws;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.core.logger.Logger;
import org.eclipse.jetty.http.HttpMethod;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.02.26
 */
abstract class WebServices implements Iterable<WebService> {

  private final List<WebService> methods = new ArrayList<>();

  void add(JSONArray jsonArray) throws PropertyNotExistException, ClassNotFoundException {
    if (jsonArray == null) {
      return;
    }
    for (JSONValue jsonValue : jsonArray) {
      JSONObject jsonObject = jsonValue.toJSONObject();
      String path = jsonObject.getString("path");
      String className = jsonObject.getString("class");
      if (className.isEmpty()) {
        Logger.debug("Skip %s %s", getMethod(), path);
      } else {
        WebService webService = new WebService(getMethod(), path, className);
        methods.add(webService);
        Logger.debug("Add: %s %s (%s)", getMethod(), path, webService.getClassName());
      }
    }
  }

  abstract HttpMethod getMethod();

  @Override
  public Iterator iterator() {
    return methods.iterator();
  }

}
