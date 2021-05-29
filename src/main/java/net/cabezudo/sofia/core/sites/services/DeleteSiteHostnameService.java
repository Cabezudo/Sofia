package net.cabezudo.sofia.core.sites.services;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.InvalidPathParameterException;
import net.cabezudo.sofia.core.WebServer;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLToken;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.sites.domainname.DomainName;
import net.cabezudo.sofia.core.sites.domainname.DomainNameManager;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.09
 */
public class DeleteSiteHostnameService extends Service {

  public DeleteSiteHostnameService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
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

    URLToken hostIdToken = tokens.getValue("hostId");
    int hostId;
    try {
      hostId = hostIdToken.toInteger();
    } catch (InvalidPathParameterException e) {
      sendError(HttpServletResponse.SC_NOT_FOUND, "Resource " + hostIdToken + " not found", e);
      return;
    }

    Site site;
    try {
      site = SiteManager.getInstance().getById(siteId);
      if (site == null) {
        sendError(HttpServletResponse.SC_NOT_FOUND, "Resource " + siteId + " not found");
        return;
      }
      DomainName baseDomainName = site.getBaseDomainName();
      if (baseDomainName.getId() == hostId) {
        sendResponse(new Response(Response.Status.ERROR, Response.Type.DELETE, "site.host.base.can.not.be.deleted"));
        return;
      }
    } catch (ClusterException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable", e);
    }

    try {
      DomainName domainName = DomainNameManager.getInstance().get(hostId);
      WebServer.delete(domainName);
      DomainNameManager.getInstance().delete(hostId);

      JSONObject data = new JSONObject();
      data.add(new JSONPair("id", hostId));
      sendResponse(new Response(Response.Status.OK, Response.Type.DELETE, data, "site.hostname.deleted"));
    } catch (ClusterException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable", e);
    }
  }
}
