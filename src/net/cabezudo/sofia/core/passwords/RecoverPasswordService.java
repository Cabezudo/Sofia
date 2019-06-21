package net.cabezudo.sofia.core.passwords;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.mail.MailServerException;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.ws.parser.tokens.Tokens;
import net.cabezudo.sofia.core.ws.responses.Message;
import net.cabezudo.sofia.core.ws.responses.Messages;
import net.cabezudo.sofia.core.ws.responses.MultipleMessageResponse;
import net.cabezudo.sofia.core.ws.responses.SingleMessageResponse;
import net.cabezudo.sofia.core.ws.servlet.services.Service;
import net.cabezudo.sofia.customers.CustomerService;
import net.cabezudo.sofia.domains.DomainMaxSizeException;
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
    String address = tokens.getValue("email").toString();
    try {
      Messages messages = EMailValidator.validate(address);
      if (messages.hasErrors()) {
        sendResponse(new MultipleMessageResponse("EMAIL_VALIDATION", messages));
      } else {
        Site site = super.getSite();
        CustomerService.sendPasswordRecoveryEMail(site, address);
        super.sendResponse(new SingleMessageResponse(new Message("password.recovery.mail.sent")));
      }
    } catch (EMailMaxSizeException | DomainMaxSizeException e) {
      Logger.warning(e);
      sendError(HttpServletResponse.SC_REQUEST_URI_TOO_LONG, e);
    } catch (SQLException | MailServerException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, e);
    } catch (IOException e) {
      sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
    }
  }
}
