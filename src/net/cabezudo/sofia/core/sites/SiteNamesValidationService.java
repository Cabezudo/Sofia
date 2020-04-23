package net.cabezudo.sofia.core.sites;

import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.system.SystemMonitor;
import net.cabezudo.sofia.core.ws.parser.tokens.Tokens;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.responses.ValidationResponse;
import net.cabezudo.sofia.core.ws.servlet.services.ValidationService;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.09
 */
public class SiteNamesValidationService extends ValidationService {

  public SiteNamesValidationService(HttpServletRequest request, HttpServletResponse response, Tokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void execute() throws ServletException {
    String name;
    name = tokens.getValue("name").toString().trim();
    if (name.isEmpty()) {
      sendResponse(new ValidationResponse(Response.Status.ERROR, "site.name.empty"));
      return;
    }
    try {
      Site siteToValidate = SiteManager.getInstance().getByName(name);
      if (siteToValidate != null) {
        sendResponse(new ValidationResponse(Response.Status.ERROR, "site.name.exist"));
        return;
      }
      sendResponse(new ValidationResponse(Response.Status.OK, "site.name.ok"));
    } catch (SQLException e) {
      SystemMonitor.log(e);
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable");
    }
  }
}
