package net.cabezudo.sofia.core.passwords;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.mail.MailServerException;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.domainname.DomainNameMaxSizeException;
import net.cabezudo.sofia.core.ws.parser.tokens.Tokens;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;
import net.cabezudo.sofia.customers.CustomerService;
import net.cabezudo.sofia.emails.EMailAddressValidationException;
import net.cabezudo.sofia.emails.EMailMaxSizeException;
import net.cabezudo.sofia.emails.EMailValidator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.18
 */
public class RecoverPasswordService extends Service {

  private final Tokens tokens;

  public RecoverPasswordService(HttpServletRequest request, HttpServletResponse response, Tokens tokens) throws ServletException {
    super(request, response);
    this.tokens = tokens;
  }

  @Override
  public void execute() throws ServletException {
    Logger.fine("Call the web service to send email to recover account.");
    try {
      String address = tokens.getValue("email").toString();
      EMailValidator.validate(address);
      Site site = super.getSite();
      CustomerService.sendPasswordRecoveryEMail(site, address);
      sendResponse(new Response(Response.Status.OK, Response.Type.ACTION, "password.recovery.mail.sent"));
    } catch (EMailMaxSizeException | DomainNameMaxSizeException e) {
      Logger.warning(e);
      sendError(HttpServletResponse.SC_REQUEST_URI_TOO_LONG, e);
    } catch (SQLException | MailServerException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, e);
    } catch (IOException e) {
      sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
    } catch (EMailAddressValidationException e) {
      sendResponse(new Response(Response.Status.ERROR, Response.Type.ACTION, e.getMessage(), e.getParameters()));
    }
  }
}
