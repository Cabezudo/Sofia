package net.cabezudo.sofia.clients;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.InvalidPathParameterException;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLToken;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.13
 */
public class DetailClientsService extends Service {

  public DetailClientsService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void get() throws ServletException {
    URLToken token = tokens.getValue("clientId");

    try {
      int id = token.toInteger();
      Client client = ClientManager.getInstance().get(id);
      out.print(client.toJSON());
    } catch (ClusterException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, e.getMessage());
    } catch (InvalidPathParameterException e) {
      sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }
}
