package net.cabezudo.sofia.core.users;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.ws.servlet.services.ListService;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.18
 */
public class UserListService extends ListService {

  public UserListService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void get() throws ServletException {
    Logger.debug("Run get method in web service %s.", this.getClass().getName());
    try {
      User owner = super.getUser();
      UserList list = UserManager.getInstance().list(super.getFilters(), super.getSort(), super.getOffset(), super.getLimit(), owner);
      out.print(list.toJSON());
    } catch (ClusterException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable", e);
    }
  }
}
