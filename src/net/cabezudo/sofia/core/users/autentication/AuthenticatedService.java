package net.cabezudo.sofia.core.users.autentication;

import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.ws.responses.AbstractMessage;
import net.cabezudo.sofia.core.ws.responses.ErrorMessage;
import net.cabezudo.sofia.core.ws.responses.Message;
import net.cabezudo.sofia.core.ws.responses.SingleMessageResponse;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.18
 */
public class AuthenticatedService extends Service {

  public AuthenticatedService(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    super(request, response);
  }

  @Override
  public void execute() throws ServletException {
    try {
      Logger.fine("Call the web service to return if the user is logged");
      if (getClientData().isLogged()) {
        Message message = new Message("login.logged");
        sendResponse(new SingleMessageResponse(message));
      } else {
        AbstractMessage message = new ErrorMessage("login.notLogged");
        sendResponse(new SingleMessageResponse(message));
      }
    } catch (SQLException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, e);
    }
  }
}
