package net.cabezudo.sofia.core.users.autentication;

import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSON;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.passwords.Password;
import net.cabezudo.sofia.core.passwords.PasswordMaxSizeException;
import net.cabezudo.sofia.core.passwords.PasswordValidationException;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.webusers.WebUserDataManager;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;
import net.cabezudo.sofia.emails.EMailAddressValidationException;
import net.cabezudo.sofia.emails.EMailMaxSizeException;
import net.cabezudo.sofia.hosts.HostMaxSizeException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.18
 */
public class LoginService extends Service {

  public LoginService(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    super(request, response);
  }

  @Override
  public void execute() throws ServletException {
    Logger.fine("Calling the web service to authorize");
    try {

      String payload = getPayload();
      JSONObject jsonPayload = JSON.parse(payload).toJSONObject();

      String email;
      try {
        email = jsonPayload.getString("email");
      } catch (PropertyNotExistException e) {
        sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing email property");
        return;
      }

      String base64Password;
      try {
        base64Password = jsonPayload.getString("password");
      } catch (PropertyNotExistException e) {
        sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing password property");
        return;
      }
      Password password = Password.createFromBase64(base64Password);

      Authenticator authenticator = new Authenticator();

      User user;
      try {
        user = authenticator.authorize(super.getSite(), email, password);
      } catch (EMailAddressValidationException | PasswordValidationException e) {
        sendResponse(new Response("ERROR", e.getMessage(), e.getParameters()));
        return;
      }
      if (user == null) {
        long loginResponseTime = getClientData().getFailLoginResponseTime();
        try {
          Thread.sleep(loginResponseTime);
        } catch (InterruptedException e) {
          sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
          return;
        }
        WebUserDataManager.getInstance().incrementFailLoginResponseTime(getClientData());
        sendResponse(new Response("FAIL", "login.fail"));
      } else {
        setClientData(WebUserDataManager.getInstance().resetFailLoginResponseTime(getClientData()));
        getClientData().setUser(user);
        request.getSession().removeAttribute("comebackPage");
        sendResponse(new Response("LOGGED", "user.logged"));
      }
    } catch (EMailMaxSizeException | PasswordMaxSizeException | HostMaxSizeException e) {
      Logger.warning(e);
      sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request.");
    } catch (SQLException sqle) {
      Logger.severe(sqle);
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "I have problems with the database. Please try in a few minutes.");
    } catch (JSONParseException e) {
      sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid payload format. " + e.getMessage());
    }
  }
}
