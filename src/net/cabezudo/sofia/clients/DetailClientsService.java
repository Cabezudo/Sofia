package net.cabezudo.sofia.clients;

import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.InvalidPathParameterException;
import net.cabezudo.sofia.core.ws.parser.tokens.Token;
import net.cabezudo.sofia.core.ws.parser.tokens.Tokens;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.13
 */
public class DetailClientsService extends Service {

  private final Tokens tokens;

  public DetailClientsService(HttpServletRequest request, HttpServletResponse response, Tokens tokens) throws ServletException {
    super(request, response);
    this.tokens = tokens;
  }

  @Override
  public void execute() throws ServletException {
    Token token = tokens.getValue("clientId");

    try {
      int id = token.toInteger();
      Client client = ClientManager.getInstance().get(id);
      out.print(client.toJSON());

    } catch (SQLException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, e.getMessage());
    } catch (InvalidPathParameterException e) {
      sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }
}
