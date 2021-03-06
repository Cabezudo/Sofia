package net.cabezudo.sofia.core.sites.services;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSON;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.InvalidPathParameterException;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLToken;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.sites.InvalidSiteVersionException;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.sites.validators.EmptySiteNameException;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 *
 * @author estebancabezudo
 */
public class SiteModifyService extends Service {

  public SiteModifyService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void get() throws ServletException {
    int siteId;

    try {
      User owner = super.getUser();

      try {
        URLToken token = tokens.getValue("siteId");
        siteId = token.toInteger();
      } catch (InvalidPathParameterException e) {
        sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found", e);
        return;
      }

      Site site = SiteManager.getInstance().getById(siteId);
      if (site == null) {
        sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
        return;
      }

      String payload = getPayload();
      JSONObject jsonData = JSON.parse(payload).toJSONObject();
      String field = jsonData.getString("field");
      String value = jsonData.getString("value");
      SiteManager.getInstance().update(siteId, field, value, owner);

      sendResponse(new Response(Response.Status.OK, Response.Type.UPDATE, "site.updated"));
    } catch (JSONParseException | PropertyNotExistException e) {
      sendError(HttpServletResponse.SC_BAD_REQUEST, e);
    } catch (ClusterException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable", e);
    } catch (InvalidSiteVersionException e) {
      sendResponse(new Response(Response.Status.ERROR, Response.Type.UPDATE, e.getMessage(), e.getParameters()));
    } catch (EmptySiteNameException e) {
      sendResponse(new Response(Response.Status.ERROR, Response.Type.UPDATE, e.getMessage()));
    }
  }

}
