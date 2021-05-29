package net.cabezudo.sofia.core.sites.services;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.InvalidPathParameterException;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.hostname.HostnameMaxSizeException;
import net.cabezudo.sofia.core.hostname.HostnameValidationException;
import net.cabezudo.sofia.core.hostname.HostnameValidator;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLToken;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.09
 */
public class SiteHostnameNameValidationService extends Service {

  public SiteHostnameNameValidationService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void get() throws ServletException {
    int siteId;
    String name;
    URLToken token;

    try {
      User owner = super.getUser();

      token = tokens.getValue("siteId");

      try {
        siteId = token.toInteger();
        name = tokens.getValue("name").toString();
      } catch (InvalidPathParameterException e) {
        sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameter: " + token.toString(), e);
        return;
      }

      Site site = SiteManager.getInstance().getById(siteId);
      if (site == null) {
        sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
        return;
      }
      Site siteWithHostname = SiteManager.getInstance().getByHostame(name);
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
    } catch (ClusterException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable", e);
    }
  }
}
