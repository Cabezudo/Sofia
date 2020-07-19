package net.cabezudo.sofia.core.ws;

import java.lang.reflect.InvocationTargetException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.core.ws.parser.tokens.WSTokens;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.02.26
 */
public class WebServicesUniverse {

  private static final WebServicesUniverse INSTANCE = new WebServicesUniverse();
  private final WebServices get = new GetWebServices();
  private final WebServices post = new PostWebServices();
  private final WebServices delete = new DeleteWebServices();
  private final WebServices put = new PutWebServices();

  public static WebServicesUniverse getInstance() {
    return INSTANCE;
  }

  private WebServicesUniverse() {
    // TODO nothing to do here
  }

  public void add(JSONObject apiConfiguration) throws PropertyNotExistException, ClassNotFoundException {
    get.add(apiConfiguration.getNullJSONArray("get"));
    post.add(apiConfiguration.getNullJSONArray("post"));
    delete.add(apiConfiguration.getNullJSONArray("delete"));
    put.add(apiConfiguration.getNullJSONArray("put"));
  }

  public void runGET(HttpServletRequest request, HttpServletResponse response, WSTokens tokens)
          throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ServletException, WebServiceNotFoundException {
    run(request, response, tokens, get);
  }

  public void runPOST(HttpServletRequest request, HttpServletResponse response, WSTokens tokens)
          throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ServletException, WebServiceNotFoundException {
    run(request, response, tokens, post);
  }

  public void runDELETE(HttpServletRequest request, HttpServletResponse response, WSTokens tokens)
          throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ServletException, WebServiceNotFoundException {
    run(request, response, tokens, delete);
  }

  public void runPUT(HttpServletRequest request, HttpServletResponse response, WSTokens tokens)
          throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ServletException, WebServiceNotFoundException {
    run(request, response, tokens, put);
  }

  private void run(HttpServletRequest request, HttpServletResponse response, WSTokens tokens, WebServices webServices)
          throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ServletException, WebServiceNotFoundException {
    for (WebService webService : webServices) {
      if (tokens.match(webService.getPath())) {
        Class<?> classToLoad = Class.forName(webService.getClassName());
        Logger.debug("Load web service class: %s.", webService.getClassName());

        Class[] arguments = new Class[3];
        arguments[0] = HttpServletRequest.class;
        arguments[1] = HttpServletResponse.class;
        arguments[2] = WSTokens.class;

        Service service = (Service) classToLoad.getDeclaredConstructor(arguments).newInstance(request, response, tokens);
        service.execute();
        return;
      }
    }
    throw new WebServiceNotFoundException("Web service NOT FOUND: " + tokens.toString());
  }
}
