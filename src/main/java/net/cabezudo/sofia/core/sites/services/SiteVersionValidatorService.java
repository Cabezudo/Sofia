package net.cabezudo.sofia.core.sites.services;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.InvalidPathParameterException;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLToken;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.sites.InvalidSiteVersionException;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.responses.ValidationResponse;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.09
 */
public class SiteVersionValidatorService extends Service {

  public SiteVersionValidatorService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void get() throws ServletException {
    int siteId;
    String versionParameter;
    URLToken token;

    token = tokens.getValue("siteId");
    try {
      siteId = token.toInteger();
      versionParameter = tokens.getValue("version").toString();
    } catch (InvalidPathParameterException e) {
      sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameter: " + token.toString(), e);
      return;
    }

    try {
      Site site = SiteManager.getInstance().getById(siteId);
      if (site == null) {
        sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
        return;
      }

      SiteManager.getInstance().validateVersion(versionParameter);
      sendResponse(new ValidationResponse(Response.Status.OK, "site.name.ok"));
    } catch (ClusterException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable", e);
    } catch (InvalidSiteVersionException e) {
      sendResponse(new ValidationResponse(Response.Status.ERROR, e));
    }
  }
}
