package net.cabezudo.sofia.core.passwords;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSON;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.mail.MailServerException;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.users.ChangePasswordException;
import net.cabezudo.sofia.core.users.UserManager;
import net.cabezudo.sofia.core.ws.parser.tokens.Tokens;
import net.cabezudo.sofia.core.ws.responses.Message;
import net.cabezudo.sofia.core.ws.responses.Messages;
import net.cabezudo.sofia.core.ws.responses.MultipleMessageResponse;
import net.cabezudo.sofia.core.ws.responses.SingleMessageResponse;
import net.cabezudo.sofia.core.ws.servlet.services.Service;
import net.cabezudo.sofia.emails.EMailNotExistException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.18
 */
public class SetPasswordService extends Service {

  private final Tokens tokens;

  public SetPasswordService(HttpServletRequest request, HttpServletResponse response, Tokens tokens) throws ServletException {
    super(request, response);
    this.tokens = tokens;
  }

  @Override
  public void execute() throws ServletException {
    Logger.fine("Change the password using a hash.");

    Hash hash = new Hash(tokens.getValue("hash").toString());
    String payload = getPayload();

    JSONObject jsonPayload;
    try {
      jsonPayload = JSON.parse(payload).toJSONObject();
    } catch (JSONParseException e) {
      sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid payload format. " + e.getMessage());
      return;
    }

    String base64Password;
    try {
      base64Password = jsonPayload.getString("password");
    } catch (PropertyNotExistException e) {
      sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing password property");
      return;
    }

    Password password;
    try {
      password = Password.createFromBase64(base64Password);
      Messages messages = PasswordValidator.validate(password);
      if (messages.hasErrors()) {
        super.sendResponse(new MultipleMessageResponse("SET_PASSWORD", messages));
        return;
      }

      Site site = super.getSite();
      UserManager.getInstance().changePassword(site, hash, password);
      super.sendResponse(new SingleMessageResponse(new Message("password.change.ok")));
    } catch (PasswordMaxSizeException e) {
      sendError(HttpServletResponse.SC_REQUEST_URI_TOO_LONG, e);
    } catch (ChangePasswordException | EMailNotExistException e) {
      sendError(HttpServletResponse.SC_PRECONDITION_FAILED, e);
    } catch (MailServerException | IOException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, e);
    } catch (SQLException e) {
      sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
    }
  }
}
