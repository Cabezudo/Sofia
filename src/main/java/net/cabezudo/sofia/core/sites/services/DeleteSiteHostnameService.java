package net.cabezudo.sofia.core.sites.services;

import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.InvalidPathParameterException;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.sites.domainname.DomainName;
import net.cabezudo.sofia.core.sites.domainname.DomainNameManager;
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
public class DeleteSiteHostnameService extends Service {

  public DeleteSiteHostnameService(HttpServletRequest request, HttpServletResponse response, WSTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void execute() throws ServletException {

    User owner = super.getUser();

    try {
      WSToken siteIdToken = tokens.getValue("siteId");
      int siteId;
      try {
        siteId = siteIdToken.toInteger();
      } catch (InvalidPathParameterException e) {
        sendError(HttpServletResponse.SC_NOT_FOUND, "Resource " + siteIdToken + " not found");
        return;
      }

      Site site = SiteManager.getInstance().getById(siteId, owner);
      if (site == null) {
        sendError(HttpServletResponse.SC_NOT_FOUND, "Resource " + siteId + " not found");
        return;
      }

      WSToken hostIdToken = tokens.getValue("hostId");
      int hostId;
      try {
        hostId = hostIdToken.toInteger();
      } catch (InvalidPathParameterException e) {
        sendError(HttpServletResponse.SC_NOT_FOUND, "Resource " + hostIdToken + " not found");
        return;
      }

      DomainName baseDomainName = site.getBaseDomainName();
      if (baseDomainName.getId() == hostId) {
        sendResponse(new Response(Response.Status.ERROR, Response.Type.DELETE, "site.host.base.can.not.be.deleted"));
        return;
      }

      DomainNameManager.getInstance().delete(hostId);
      JSONObject data = new JSONObject();
      data.add(new JSONPair("id", hostId));
      sendResponse(new Response(Response.Status.OK, Response.Type.DELETE, data, "site.host.deleted"));
    } catch (SQLException e) {
      SystemMonitor.log(e);
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable");
    }
  }
}
