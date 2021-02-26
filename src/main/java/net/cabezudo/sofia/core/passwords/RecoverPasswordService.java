package net.cabezudo.sofia.core.passwords;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.configuration.ConfigurationException;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.mail.MailServerException;
import net.cabezudo.sofia.core.sites.domainname.DomainNameMaxSizeException;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;
import net.cabezudo.sofia.customers.CustomerService;
import net.cabezudo.sofia.emails.EMailAddressValidationException;
import net.cabezudo.sofia.emails.EMailMaxSizeException;
import net.cabezudo.sofia.emails.EMailValidator;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.18
 */
public class RecoverPasswordService extends Service {

  public RecoverPasswordService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void get() throws ServletException {
    Logger.fine("Call the web service to send email to recover account.");
    try {
      String address = tokens.getValue("email").toString();
      EMailValidator.validate(address);
      CustomerService.sendPasswordRecoveryEMail(site, address);
      sendResponse(new Response(Response.Status.OK, Response.Type.ACTION, "password.recovery.mail.sent"));
    } catch (EMailMaxSizeException | DomainNameMaxSizeException e) {
      Logger.warning(e);
      sendError(HttpServletResponse.SC_REQUEST_URI_TOO_LONG, e);
    } catch (ClusterException | MailServerException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, e);
    } catch (ConfigurationException | IOException e) {
      sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
    } catch (EMailAddressValidationException e) {
      sendResponse(new Response(Response.Status.ERROR, Response.Type.ACTION, e.getMessage(), e.getParameters()));
    }
  }
}
