package net.cabezudo.sofia.core.sites;

import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.InvalidPathParameterException;
import net.cabezudo.sofia.core.system.SystemMonitor;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.ws.parser.tokens.Token;
import net.cabezudo.sofia.core.ws.parser.tokens.Tokens;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.09
 */
public class SiteVersionService extends Service {

  private final Tokens tokens;

  public SiteVersionService(HttpServletRequest request, HttpServletResponse response, Tokens tokens) throws ServletException {
    super(request, response);
    this.tokens = tokens;
  }

  @Override
  public void execute() throws ServletException {
    int siteId;
    int version;
    Token token;

    User owner = super.getUser();

    token = tokens.getValue("siteId");
    try {
      siteId = token.toInteger();
    } catch (InvalidPathParameterException e) {
      sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameter: " + token.toString());
      return;
    }

    token = tokens.getValue("version");
    try {
      Site site = SiteManager.getInstance().getById(siteId, owner);
      if (site == null) {
        sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
        return;
      }

      version = token.toInteger();

      site = new Site(site.getId(), site.getName(), site.getBaseDomainName(), site.getDomainNames(), version);
      SiteManager.getInstance().update(site, owner);

      sendResponse(new Response("OK", Response.Type.VALIDATION, "site.name.ok"));
    } catch (InvalidPathParameterException e) {
      sendResponse(new Response("ERROR", Response.Type.VALIDATION, "site.name.exist"));
    } catch (SQLException e) {
      SystemMonitor.log(e);
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable");
    }
  }
}
