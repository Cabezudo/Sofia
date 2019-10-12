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
import net.cabezudo.sofia.hostname.HostnameMaxSizeException;
import net.cabezudo.sofia.hostname.HostnameValidationException;
import net.cabezudo.sofia.hostname.HostnameValidator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.09
 */
public class SiteHostnameNameValidationService extends Service {

  private final Tokens tokens;

  public SiteHostnameNameValidationService(HttpServletRequest request, HttpServletResponse response, Tokens tokens) throws ServletException {
    super(request, response);
    this.tokens = tokens;
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
      Site siteWithHostname = SiteManager.getInstance().getByHostame(name, owner);
      if (siteWithHostname != null && siteWithHostname.getId() != siteId) {
        sendResponse(new Response("ERROR", Response.Type.VALIDATION, "site.hostname.exist.for.other.site", name, siteWithHostname.getName()));
        return;
      }

      try {
        HostnameValidator.validate(name);
      } catch (HostnameMaxSizeException e) {
        sendResponse(new Response("ERROR", Response.Type.VALIDATION, e.getMessage()));
        return;
      } catch (HostnameValidationException e) {
        sendResponse(new Response("ERROR", Response.Type.VALIDATION, e.getMessage(), e.getParameters()));
        return;
      }

      sendResponse(new Response("OK", Response.Type.VALIDATION, "site.name.ok"));
    } catch (SQLException e) {
      SystemMonitor.log(e);
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable");
    }
  }
}
