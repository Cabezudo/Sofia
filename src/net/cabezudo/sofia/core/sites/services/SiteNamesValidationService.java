package net.cabezudo.sofia.core.sites.services;

import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.InvalidPathParameterException;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.sites.SiteValidationException;
import net.cabezudo.sofia.core.sites.validators.SiteNameValidator;
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
public class SiteNamesValidationService extends Service {

  public SiteNamesValidationService(HttpServletRequest request, HttpServletResponse response, Tokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void execute() throws ServletException {
    int siteId;
    String name;
    Token token;

    User owner = super.getUser();

    token = tokens.getValue("siteId");

    try {
      siteId = token.toInteger();
      name = tokens.getValue("name").toString();
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
      Site siteToValidate = SiteManager.getInstance().getByName(name);
      if (siteToValidate != null && siteToValidate.getId() != siteId) {
        sendResponse(new ValidationResponse(Response.Status.ERROR, "site.name.exist"));
        return;
      }
      SiteNameValidator.getInstance().validate(name);
      sendResponse(new ValidationResponse(Response.Status.OK, "site.name.ok"));
    } catch (SiteValidationException e) {
      sendResponse(new Response(Response.Status.ERROR, Response.Type.VALIDATION, e.getMessage()));
      return;
    } catch (SQLException e) {
      SystemMonitor.log(e);
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable");
    }
  }
}
