package net.cabezudo.sofia.core.users.autentication;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.18
 */
public class AuthenticatedService extends Service {

  public AuthenticatedService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void get() throws ServletException {
    Logger.fine("Call the web service to return if the user is logged");
    if (webUserData.isLogged()) {
      sendResponse(new Response(Response.Status.OK, Response.Type.ACTION, "login.logged"));
    } else {
      sendResponse(new Response(Response.Status.ERROR, Response.Type.ACTION, "login.notLogged"));
    }
  }
}
