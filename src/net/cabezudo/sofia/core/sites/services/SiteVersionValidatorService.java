package net.cabezudo.sofia.core.sites.services;

import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.InvalidPathParameterException;
import net.cabezudo.sofia.core.sites.InvalidSiteVersionException;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.system.SystemMonitor;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.ws.parser.tokens.Token;
import net.cabezudo.sofia.core.ws.parser.tokens.Tokens;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.responses.ValidationResponse;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.09
 */
public class SiteVersionValidatorService extends Service {

  public SiteVersionValidatorService(HttpServletRequest request, HttpServletResponse response, Tokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void execute() throws ServletException {
    int siteId;
    String versionParameter;
    Token token;

    User owner = super.getUser();

    token = tokens.getValue("siteId");
    try {
      siteId = token.toInteger();
      versionParameter = tokens.getValue("version").toString();
    } catch (InvalidPathParameterException e) {
      sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameter: " + token.toString());
      return;
    }

    try {
      Site site = SiteManager.getInstance().getById(siteId, owner);
      if (site == null) {
        sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
        return;
      }

      SiteManager.getInstance().validateVersion(versionParameter);
      sendResponse(new ValidationResponse(Response.Status.OK, "site.name.ok"));
    } catch (SQLException e) {
      SystemMonitor.log(e);
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable");
    } catch (InvalidSiteVersionException e) {
      sendResponse(new ValidationResponse(Response.Status.ERROR, e));
    }
  }
}
