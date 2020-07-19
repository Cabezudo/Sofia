package net.cabezudo.sofia.core.users.autentication;

import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.core.ws.parser.tokens.WSTokens;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.18
 */
public class AuthenticatedService extends Service {

  public AuthenticatedService(HttpServletRequest request, HttpServletResponse response, WSTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void execute() throws ServletException {
    try {
      Logger.fine("Call the web service to return if the user is logged");
      if (getClientData().isLogged()) {
        sendResponse(new Response(Response.Status.OK, Response.Type.ACTION, "login.logged"));
      } else {
        sendResponse(new Response(Response.Status.ERROR, Response.Type.ACTION, "login.notLogged"));
      }
    } catch (SQLException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, e);
    }
  }
}
