package net.cabezudo.sofia.emails;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.ws.parser.tokens.Tokens;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;
import net.cabezudo.sofia.core.sites.domainname.DomainNameMaxSizeException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.17
 */
public class EMailValidatorService extends Service {

  private final Tokens tokens;

  public EMailValidatorService(HttpServletRequest request, HttpServletResponse response, Tokens tokens) throws ServletException {
    super(request, response);
    this.tokens = tokens;
  }

  @Override
  public void execute() throws ServletException {
    String address = tokens.getValue("email").toString();
    try {
      String messageKey = EMailValidator.validate(address);
      sendResponse(new Response("OK", messageKey));
    } catch (EMailMaxSizeException | DomainNameMaxSizeException e) {
      Logger.warning(e);
      super.sendError(HttpServletResponse.SC_REQUEST_URI_TOO_LONG, e);
    } catch (EMailAddressValidationException e) {
      sendResponse(new Response("ERROR", e.getMessage(), e.getParameters()));
    }
  }
}
