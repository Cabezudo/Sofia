package net.cabezudo.sofia.core.sites.services;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.InvalidPathParameterException;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLToken;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.09
 */
public class DeleteSiteService extends Service {

  public DeleteSiteService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void delete() throws ServletException {

    URLToken siteIdToken = tokens.getValue("siteId");
    int siteId;

    try {
      siteId = siteIdToken.toInteger();
    } catch (InvalidPathParameterException e) {
      sendError(HttpServletResponse.SC_NOT_FOUND, "Resource " + siteIdToken + " not found", e);
      return;
    }

    try {
      if (siteId == 1 || siteId == 2) {
        sendError(HttpServletResponse.SC_FORBIDDEN, "The resource " + siteId + " can't be deleted.");
        return;
      }

      Site site = SiteManager.getInstance().getById(siteId);
      if (site == null) {
        sendError(HttpServletResponse.SC_NOT_FOUND, "Resource " + siteId + " not found");
        return;
      }

      SiteManager.getInstance().delete(siteId);
      JSONObject data = new JSONObject();
      data.add(new JSONPair("id", siteId));
      sendResponse(new Response(Response.Status.OK, Response.Type.DELETE, data, "site.deleted"));
    } catch (ClusterException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable", e);
    }
  }
}
