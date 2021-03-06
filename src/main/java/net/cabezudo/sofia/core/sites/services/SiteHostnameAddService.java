package net.cabezudo.sofia.core.sites.services;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;
import net.cabezudo.json.JSON;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.InvalidPathParameterException;
import net.cabezudo.sofia.core.WebServer;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.database.sql.Database;
import net.cabezudo.sofia.core.hostname.HostnameMaxSizeException;
import net.cabezudo.sofia.core.hostname.HostnameValidationException;
import net.cabezudo.sofia.core.hostname.HostnameValidator;
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
public class SiteHostnameAddService extends Service {

  public SiteHostnameAddService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void post() throws ServletException {

    URLToken siteIdToken = tokens.getValue("siteId");

    int siteId;

    try {
      siteId = siteIdToken.toInteger();
    } catch (InvalidPathParameterException e) {
      sendError(HttpServletResponse.SC_NOT_FOUND, "Resource " + siteIdToken + " not found", e);
      return;
    }

    try {
      Site site = SiteManager.getInstance().getById(siteId);
      if (site == null) {
        sendError(HttpServletResponse.SC_NOT_FOUND, "Resource " + siteId + " not found");
        return;
      }
      Connection connection = Database.getConnection();
      String payload = getPayload();
      JSONObject jsonData = JSON.parse(payload).toJSONObject();
      String hostname = jsonData.getString("name");
      String messageKey = HostnameValidator.getInstance().validate(hostname);

      DomainName domainName = DomainNameManager.getInstance().add(connection, site.getId(), hostname);
      WebServer.addHostNameToVirtualHost(domainName);
      JSONObject data = new JSONObject();
      data.add(new JSONPair("id", domainName.getId()));
      data.add(new JSONPair("name", domainName.getName()));
      sendResponse(new Response(Response.Status.OK, Response.Type.CREATE, data, "site.hostname.addeded", domainName.getName(), site.getName()));
    } catch (JSONParseException | PropertyNotExistException | HostnameMaxSizeException e) {
      sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), e);
    } catch (SQLException | ClusterException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable", e);
    } catch (HostnameValidationException e) {
      sendResponse(new Response(Response.Status.ERROR, Response.Type.UPDATE, e.getMessage(), e.getParameters()));
    }
  }
}
