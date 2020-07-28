package net.cabezudo.sofia.core.sites.services;

import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.InvalidPathParameterException;
import net.cabezudo.sofia.core.hostname.HostnameMaxSizeException;
import net.cabezudo.sofia.core.hostname.HostnameValidationException;
import net.cabezudo.sofia.core.hostname.HostnameValidator;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.system.SystemMonitor;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.ws.parser.tokens.WSToken;
import net.cabezudo.sofia.core.ws.parser.tokens.WSTokens;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.09
 */
public class SiteHostnameNameValidationService extends Service {

  public SiteHostnameNameValidationService(HttpServletRequest request, HttpServletResponse response, WSTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void execute() throws ServletException {
    int siteId;
    String name;
    WSToken token;

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
        sendResponse(new Response(Response.Status.ERROR, Response.Type.VALIDATION, "site.hostname.exist.for.other.site", name, siteWithHostname.getName()));
        return;
      }

      try {
        HostnameValidator.getInstance().validate(name);
      } catch (HostnameMaxSizeException e) {
        sendResponse(new Response(Response.Status.ERROR, Response.Type.VALIDATION, e.getMessage()));
        return;
      } catch (HostnameValidationException e) {
        sendResponse(new Response(Response.Status.ERROR, Response.Type.VALIDATION, e.getMessage(), e.getParameters()));
        return;
      }

      sendResponse(new Response(Response.Status.OK, Response.Type.VALIDATION, "site.name.ok"));
    } catch (SQLException e) {
      SystemMonitor.log(e);
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable");
    }
  }
}