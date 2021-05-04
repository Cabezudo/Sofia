package net.cabezudo.sofia.core.sites.services;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSON;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.hostname.HostnameMaxSizeException;
import net.cabezudo.sofia.core.hostname.HostnameValidationException;
import net.cabezudo.sofia.core.hostname.HostnameValidator;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.sites.SiteValidationException;
import net.cabezudo.sofia.core.sites.validators.SiteNameValidator;
import net.cabezudo.sofia.core.sites.validators.SiteVersionException;
import net.cabezudo.sofia.core.sites.validators.SiteVersionValidator;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.09
 */
public class AddSiteService extends Service {

  public AddSiteService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void post() throws ServletException {
    JSONObject data;
    try {
      data = JSON.parse(getPayload()).toJSONObject();
    } catch (JSONParseException e) {
      throw new ServletException(e);
    }
    // TODO Validte name for security
    String name = data.getNullString("name");
    try {
      SiteNameValidator.getInstance().validate(name);
    } catch (SiteValidationException e) {
      sendResponse(new Response(Response.Status.ERROR, Response.Type.VALIDATION, e.getMessage()));
      return;
    }

    String version = data.getNullString("version");
    try {
      SiteVersionValidator.getInstance().validate(version);
    } catch (SiteVersionException e) {
      sendResponse(new Response(Response.Status.ERROR, Response.Type.VALIDATION, e.getMessage(), e.getParameters()));
      return;
    }

    String hostname = data.getNullString("hostname");
    try {
      HostnameValidator.getInstance().validate(hostname);
    } catch (HostnameMaxSizeException e) {
      sendResponse(new Response(Response.Status.ERROR, Response.Type.VALIDATION, e.getMessage()));
      return;
    } catch (HostnameValidationException e) {
      sendResponse(new Response(Response.Status.ERROR, Response.Type.VALIDATION, e.getMessage(), e.getParameters()));
      return;
    }

    // TODO Validte basePathName for security
    String basePathName = data.getNullString("basePathName");
    if (basePathName == null) {
      basePathName = hostname;
    }
    Path basePath = Paths.get(basePathName);

    try {
      SiteManager.getInstance().create(name, basePath, hostname);
    } catch (ClusterException | IOException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable", e);
    }
    sendResponse(new Response(Response.Status.OK, Response.Type.CREATE, "site.created"));
  }
}
