package net.cabezudo.sofia.people;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserNotExistException;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.18
 */
public class PeopleListService extends Service {

  public PeopleListService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void get() throws ServletException {
    try {
      User owner = super.getUser();
      if (owner == null) {
        sendError(HttpServletResponse.SC_FORBIDDEN, "Not logged");
        return;
      }
      PeopleList list = PeopleManager.getInstance().list(owner);
      out.print(list.toJSON());
    } catch (ClusterException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, e);
    } catch (UserNotExistException e) {
      sendError(HttpServletResponse.SC_PRECONDITION_FAILED, e);
    }
  }
}
