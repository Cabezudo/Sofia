package net.cabezudo.sofia.core.sites;

import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.InvalidPathParameterException;
import net.cabezudo.sofia.core.system.SystemMonitor;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.ws.parser.tokens.WSToken;
import net.cabezudo.sofia.core.ws.parser.tokens.WSTokens;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.24.10
 *
 */
public class SiteService extends Service {

  public SiteService(HttpServletRequest request, HttpServletResponse response, WSTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void execute() throws ServletException {
    User owner = super.getUser();

    WSToken token = tokens.getValue("siteId");
    int siteId;
    try {
      siteId = token.toInteger();
    } catch (InvalidPathParameterException e) {
      sendError(HttpServletResponse.SC_NOT_FOUND, "Resource " + token + " not found");
      return;
    }

    try {
      Site site = SiteManager.getInstance().getById(siteId, owner);
      if (site == null) {
        sendError(HttpServletResponse.SC_NOT_FOUND, "Resource " + siteId + " not found");
        return;
      }
      out.print(site.toJSON());
    } catch (SQLException e) {
      SystemMonitor.log(e);
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable");
    }
  }
}
