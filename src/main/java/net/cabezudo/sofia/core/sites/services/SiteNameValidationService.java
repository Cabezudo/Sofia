package net.cabezudo.sofia.core.sites.services;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.sites.SiteValidationException;
import net.cabezudo.sofia.core.sites.validators.SiteNameValidator;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.responses.ValidationResponse;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.09
 */
public class SiteNameValidationService extends Service {

  public SiteNameValidationService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void get() throws ServletException {
    String name;

    try {
      name = tokens.getValue("name").toString();

      Site siteToValidate = SiteManager.getInstance().getByName(name);
      if (siteToValidate != null) {
        sendResponse(new ValidationResponse(Response.Status.ERROR, "site.name.exist"));
        return;
      }
      SiteNameValidator.getInstance().validate(name);
      sendResponse(new ValidationResponse(Response.Status.OK, "site.name.ok"));
    } catch (SiteValidationException e) {
      sendResponse(new Response(Response.Status.ERROR, Response.Type.VALIDATION, e.getMessage()));
    } catch (ClusterException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable", e);
    }
  }
}
