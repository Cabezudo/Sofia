package net.cabezudo.sofia.core.passwords;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSON;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.18
 */
public class PasswordValidatorService extends Service {

  public PasswordValidatorService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void execute() throws ServletException {
    String payload = getPayload();
    try {
      JSONObject jsonPayload = JSON.parse(payload).toJSONObject();
      String base64Password = jsonPayload.getString("password");
      Password password = Password.createFromBase64(base64Password);
      String messageKey = PasswordValidator.validate(password);
      sendResponse(new Response(Response.Status.OK, Response.Type.VALIDATION, messageKey));
    } catch (PasswordMaxSizeException e) {
      sendError(HttpServletResponse.SC_REQUEST_URI_TOO_LONG, e);
    } catch (JSONParseException | PropertyNotExistException e) {
      sendError(HttpServletResponse.SC_BAD_REQUEST, e);
    } catch (PasswordValidationException e) {
      sendResponse(new Response(Response.Status.ERROR, Response.Type.VALIDATION, e.getMessage(), e.getParameters()));
    }
  }

}
