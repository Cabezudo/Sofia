package net.cabezudo.sofia.core.passwords;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import net.cabezudo.json.JSON;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.mail.MailServerException;
import net.cabezudo.sofia.core.users.HashTooOldException;
import net.cabezudo.sofia.core.users.NullHashException;
import net.cabezudo.sofia.core.users.UserManager;
import net.cabezudo.sofia.core.users.UserNotFoundByHashException;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;
import net.cabezudo.sofia.emails.EMailNotExistException;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.18
 */
public class SetPasswordService extends Service {

  public SetPasswordService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void post() throws ServletException {
    Logger.fine("Change the password using a hash.");

    Hash hash = new Hash(tokens.getValue("hash").toString());
    String payload = getPayload();

    JSONObject jsonPayload;
    try {
      jsonPayload = JSON.parse(payload).toJSONObject();
    } catch (JSONParseException e) {
      sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid payload format. " + e.getMessage(), e);
      return;
    }

    String base64Password;
    try {
      base64Password = jsonPayload.getString("password");
    } catch (PropertyNotExistException e) {
      sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing password property", e);
      return;
    }

    Password password;
    try {
      password = Password.createFromBase64(base64Password);
      PasswordValidator.validate(password);
      UserManager.getInstance().changePassword(site, hash, password);
      super.sendResponse(new Response(Response.Status.OK, Response.Type.SET, "password.change.ok"));
    } catch (PasswordMaxSizeException e) {
      sendError(HttpServletResponse.SC_REQUEST_URI_TOO_LONG, e);
    } catch (UserNotFoundByHashException | HashTooOldException | EMailNotExistException | MailServerException | IOException e) {
      super.sendResponse(new Response(Response.Status.ERROR, Response.Type.SET, e.getMessage()));
    } catch (ClusterException | NullHashException e) {
      sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
    } catch (PasswordValidationException e) {
      super.sendResponse(new Response(Response.Status.ERROR, Response.Type.SET, e.getMessage(), e.getParameters()));
    }
  }
}
