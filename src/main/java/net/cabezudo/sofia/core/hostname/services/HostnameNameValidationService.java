package net.cabezudo.sofia.core.hostname.services;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.hostname.HostnameMaxSizeException;
import net.cabezudo.sofia.core.hostname.HostnameValidationException;
import net.cabezudo.sofia.core.hostname.HostnameValidator;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.system.SystemMonitor;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.09
 */
public class HostnameNameValidationService extends Service {

  public HostnameNameValidationService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void get() throws ServletException {
    String name = tokens.getValue("name").toString();

    try {
      Site siteWithHostname = SiteManager.getInstance().getByHostame(name);
      if (siteWithHostname != null) {
        sendResponse(new Response(Response.Status.ERROR, Response.Type.VALIDATION, "site.hostname.exist.for.other.site", name, siteWithHostname.getName()));
        return;
      }
    } catch (ClusterException e) {
      SystemMonitor.log(e);
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable");
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
  }
}
