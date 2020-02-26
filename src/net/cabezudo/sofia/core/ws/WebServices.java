package net.cabezudo.sofia.core.ws;

import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONObject;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.02.26
 */
public class WebServices {

  private static final WebServices INSTANCE = new WebServices();
  private final WebServicesMethods get = new WebServicesMethods();
  private final WebServicesMethods post = new WebServicesMethods();
  private final WebServicesMethods delete = new WebServicesMethods();
  private final WebServicesMethods put = new WebServicesMethods();

  public static WebServices getInstance() {
    return INSTANCE;
  }

  private WebServices() {
    // TODO nothing to do here
  }

  public void add(JSONObject apiConfiguration) throws PropertyNotExistException {
    get.add(apiConfiguration.getNullJSONArray("get"));
    post.add(apiConfiguration.getNullJSONArray("post"));
    delete.add(apiConfiguration.getNullJSONArray("delete"));
    put.add(apiConfiguration.getNullJSONArray("put"));
  }
}
