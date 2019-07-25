package net.cabezudo.sofia.core.passwords;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
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
import net.cabezudo.sofia.core.users.HashTooOldException;
import net.cabezudo.sofia.core.users.NullHashException;
import net.cabezudo.sofia.core.users.UserManager;
import net.cabezudo.sofia.core.users.UserNotFoundByHashException;
import net.cabezudo.sofia.core.ws.parser.tokens.Tokens;
import net.cabezudo.sofia.core.ws.responses.ErrorMessage;
import net.cabezudo.sofia.core.ws.responses.InternalServerErrorMessage;
import net.cabezudo.sofia.core.ws.responses.Message;
import net.cabezudo.sofia.core.ws.responses.Messages;
import net.cabezudo.sofia.core.ws.responses.MultipleMessageResponse;
import net.cabezudo.sofia.core.ws.responses.ServiceUnavailableMessage;
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
    Site site = super.getSite();
    Locale locale;
    Messages messages = new Messages();
    try {
      locale = super.getClientData().getLocale();
    } catch (SQLException e) {
      sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
      return;
    }
    try {
      password = Password.createFromBase64(base64Password);
      messages.add(PasswordValidator.validate(password));
      messages = new Messages();
      if (messages.hasErrors()) {
        super.sendResponse(new MultipleMessageResponse("SET_PASSWORD", messages));
        return;
      }
      UserManager.getInstance().changePassword(site, hash, password);
      super.sendResponse(new SingleMessageResponse(new Message("password.change.ok")));
    } catch (PasswordMaxSizeException e) {
      sendError(HttpServletResponse.SC_REQUEST_URI_TOO_LONG, e);
    } catch (UserNotFoundByHashException | HashTooOldException | EMailNotExistException e) {
      super.sendResponse(new SingleMessageResponse(new ErrorMessage(e.getMessage())));
    } catch (MailServerException | IOException e) {
      super.sendResponse(new SingleMessageResponse(new ServiceUnavailableMessage(e.getMessage())));
    } catch (SQLException | NullHashException e) {
      super.sendResponse(new SingleMessageResponse(new InternalServerErrorMessage(e.getMessage())));
    }
    // If we add some lines here MUST put return to the sendError in the catch
  }
}
