package net.cabezudo.sofia.core.users.autentication;

import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSON;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.passwords.Password;
import net.cabezudo.sofia.core.passwords.PasswordMaxSizeException;
import net.cabezudo.sofia.core.passwords.PasswordValidationException;
import net.cabezudo.sofia.core.sites.domainname.DomainNameMaxSizeException;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.webusers.WebUserDataManager;
import net.cabezudo.sofia.core.webusers.WebUserDataManager.WebUserData;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;
import net.cabezudo.sofia.emails.EMailAddressValidationException;
import net.cabezudo.sofia.emails.EMailMaxSizeException;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.18
 */
public class LoginService extends Service {

  public LoginService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void post() throws ServletException {
    Logger.fine("Calling the web service to authorize");
    JSONObject jsonPayload = null;
    try {
      String payload = getPayload();
      jsonPayload = JSON.parse(payload).toJSONObject();
    } catch (JSONParseException e) {
      sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid payload format. " + e.getMessage());
      return;
    }

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

    User user = null;
    try {
      user = authenticator.authorize(super.getSite(), email, password);
    } catch (SQLException sqle) {
      Logger.severe(sqle);
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "I have problems with the database. Please try in a few minutes.");
    } catch (EMailMaxSizeException | PasswordMaxSizeException | DomainNameMaxSizeException e) {
      Logger.warning(e);
      sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request.");
    } catch (EMailAddressValidationException | PasswordValidationException e) {
      sendResponse(new Response(Response.Status.ERROR, Response.Type.ACTION, e.getMessage(), e.getParameters()));
      return;
    }
    if (user == null) {
      Logger.debug("User not found.");
      try {
        long loginResponseTime = getWebUserData().getFailLoginResponseTime();
        Thread.sleep(loginResponseTime);
        WebUserDataManager.getInstance().incrementFailLoginResponseTime(getWebUserData());
      } catch (SQLException sqle) {
        Logger.severe(sqle);
        sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "I have problems with the database. Please try in a few minutes.");
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        return;
      }
      sendResponse(new Response(Response.Status.FAIL, Response.Type.ACTION, "login.fail"));
    } else {
      Logger.debug("User found: %s", user.getId());
      try {
        WebUserData webUserData = getWebUserData();
        webUserData.setUser(user);
        WebUserDataManager.getInstance().updateUser(webUserData);
      } catch (SQLException sqle) {
        Logger.severe(sqle);
        sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "I have problems with the database. Please try in a few minutes.");
        return;
      }
      request.getSession().removeAttribute("goBackPage");
      sendResponse(new Response(Response.Status.LOGGED, Response.Type.ACTION, "user.logged"));
    }
  }
}
