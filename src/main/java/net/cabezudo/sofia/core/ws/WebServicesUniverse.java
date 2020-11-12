package net.cabezudo.sofia.core.ws;

import java.lang.reflect.InvocationTargetException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.sofia.core.APIConfiguration;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.ws.servlet.services.Service;
import net.cabezudo.sofia.logger.Logger;

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
    // Nothing to do here
  }

// TODO add web services for site using all the site hostnames
  public void add(APIConfiguration apiConfiguration) throws PropertyNotExistException, ClassNotFoundException {
    get.add(apiConfiguration.get("get"));
    post.add(apiConfiguration.get("post"));
    delete.add(apiConfiguration.get("delete"));
    put.add(apiConfiguration.get("put"));
  }

  public void runGET(HttpServletRequest request, HttpServletResponse response, URLTokens tokens)
          throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ServletException, WebServiceNotFoundException {
    run(request, response, tokens, get);
  }

  public void runPOST(HttpServletRequest request, HttpServletResponse response, URLTokens tokens)
          throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ServletException, WebServiceNotFoundException {
    run(request, response, tokens, post);
  }

  public void runDELETE(HttpServletRequest request, HttpServletResponse response, URLTokens tokens)
          throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ServletException, WebServiceNotFoundException {
    run(request, response, tokens, delete);
  }

  public void runPUT(HttpServletRequest request, HttpServletResponse response, URLTokens tokens)
          throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ServletException, WebServiceNotFoundException {
    run(request, response, tokens, put);
  }

  private void run(HttpServletRequest request, HttpServletResponse response, URLTokens tokens, WebServices webServices)
          throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ServletException, WebServiceNotFoundException {
    for (WebService webService : webServices) {
      if (tokens.match(webService.getPath())) {
        Class<?> classToLoad = Class.forName(webService.getClassName());
        Logger.debug("Load web service class: %s.", webService.getClassName());

        Class[] arguments = new Class[3];
        arguments[0] = HttpServletRequest.class;
        arguments[1] = HttpServletResponse.class;
        arguments[2] = URLTokens.class;

        Service service = (Service) classToLoad.getDeclaredConstructor(arguments).newInstance(request, response, tokens);
        switch (webService.getMethod()) {
          case GET:
            service.get();
            break;
          case POST:
            service.post();
            break;
          case PUT:
            service.put();
            break;
          case OPTIONS:
            service.options();
            break;
          case DELETE:
            service.delete();
            break;
          default:
            throw new WebServiceNotFoundException("Invalid web service method: " + webService.getMethod());
        }
        return;
      }
    }
    throw new WebServiceNotFoundException("Web service NOT FOUND: " + tokens.toString());
  }
}
