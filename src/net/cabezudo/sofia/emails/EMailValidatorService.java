package net.cabezudo.sofia.emails;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.domainname.DomainNameMaxSizeException;
import net.cabezudo.sofia.core.ws.parser.tokens.Tokens;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.17
 */
public class EMailValidatorService extends Service {

  public EMailValidatorService(HttpServletRequest request, HttpServletResponse response, Tokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void execute() throws ServletException {
    String address = tokens.getValue("email").toString();
    try {
      String messageKey = EMailValidator.validate(address);
      sendResponse(new Response(Response.Status.OK, Response.Type.VALIDATION, messageKey));
    } catch (EMailMaxSizeException | DomainNameMaxSizeException e) {
      Logger.warning(e);
      super.sendError(HttpServletResponse.SC_REQUEST_URI_TOO_LONG, e);
    } catch (EMailAddressValidationException e) {
      sendResponse(new Response(Response.Status.ERROR, Response.Type.VALIDATION, e.getMessage(), e.getParameters()));
    }
  }
}
