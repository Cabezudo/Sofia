package net.cabezudo.sofia.core.sites;

import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSON;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.system.SystemMonitor;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.ws.parser.tokens.Tokens;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;
import net.cabezudo.sofia.domainname.DomainName;
import net.cabezudo.sofia.domainname.DomainNameManager;
import net.cabezudo.sofia.hostname.HostnameMaxSizeException;
import net.cabezudo.sofia.hostname.HostnameValidationException;
import net.cabezudo.sofia.hostname.HostnameValidator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.24.10
 *
 */
public class SiteModifyDomainNameService extends Service {

  private final int siteId;
  private final int domainNameId;

  public SiteModifyDomainNameService(HttpServletRequest request, HttpServletResponse response, Tokens tokens) throws ServletException {
    super(request, response);

    siteId = tokens.getValue("siteId").toInteger();
    domainNameId = tokens.getValue("domainNameId").toInteger();
  }

  @Override
  public void execute() throws ServletException {
    try {
      Site site = SiteManager.getInstance().getById(siteId);
      if (site == null) {
        sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
        return;
      }
      User owner = super.getUser();

      String payload = getPayload();
      JSONObject jsonData = JSON.parse(payload).toJSONObject();
      String domainNameName = jsonData.getString("value");
      String messageKey = HostnameValidator.validate(domainNameName);

      DomainName domainName = new DomainName(domainNameId, siteId, domainNameName);

      DomainNameManager.getInstance().update(domainName, owner);

      sendResponse(new Response("OK", messageKey, domainNameName));
    } catch (JSONParseException | PropertyNotExistException | HostnameMaxSizeException e) {
      sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    } catch (SQLException e) {
      SystemMonitor.log(e);
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable");
    } catch (HostnameValidationException e) {
      sendResponse(new Response("ERROR", e.getMessage(), e.getParameters()));
    }
  }
}
