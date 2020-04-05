package net.cabezudo.sofia.core.users;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSON;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.mail.MailServerException;
import net.cabezudo.sofia.core.passwords.Password;
import net.cabezudo.sofia.core.passwords.PasswordMaxSizeException;
import net.cabezudo.sofia.core.passwords.PasswordValidationException;
import net.cabezudo.sofia.core.passwords.PasswordValidator;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.domainname.DomainNameMaxSizeException;
import net.cabezudo.sofia.core.ws.parser.tokens.Tokens;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;
import net.cabezudo.sofia.customers.CustomerService;
import net.cabezudo.sofia.emails.EMailAddressNotExistException;
import net.cabezudo.sofia.emails.EMailAddressValidationException;
import net.cabezudo.sofia.emails.EMailMaxSizeException;
import net.cabezudo.sofia.emails.EMailValidator;
import net.cabezudo.sofia.names.LastNameManager;
import net.cabezudo.sofia.names.NameManager;
import net.cabezudo.sofia.people.PeopleManager;
import net.cabezudo.sofia.people.Person;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.17
 */
public class AddUserService extends Service {

  public AddUserService(HttpServletRequest request, HttpServletResponse response, Tokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void execute() throws ServletException {

    User owner = super.getUser();
    Site site = super.getSite();

    try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
      String payload = getPayload();
      JSONObject jsonPayload = JSON.parse(payload).toJSONObject();

      String name;
      try {
        name = jsonPayload.getString("name");
        NameManager.getInstance().validate(name);
      } catch (PropertyNotExistException e) {
        super.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing name property");
        return;
      }
      String lastName;
      try {
        lastName = jsonPayload.getString("lastName");
        LastNameManager.getInstance().validate(lastName);
      } catch (PropertyNotExistException e) {
        super.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing lastName property");
        return;
      }
      String address;
      try {
        address = jsonPayload.getString("email");
        EMailValidator.validate(address);
      } catch (EMailMaxSizeException | DomainNameMaxSizeException e) {
        Logger.warning(e);
        sendError(HttpServletResponse.SC_REQUEST_URI_TOO_LONG, e);
        return;
      } catch (PropertyNotExistException e) {
        super.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing email property");
        return;
      } catch (EMailAddressValidationException e) {
        sendResponse(new Response(Response.Status.ERROR, Response.Type.CREATE, e.getMessage(), e.getParameters()));
        return;
      }
      String base64Password;
      try {
        base64Password = jsonPayload.getString("password");
      } catch (PropertyNotExistException e) {
        super.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing password property");
        return;
      }
      Password password;
      try {
        password = Password.createFromBase64(base64Password);
        PasswordValidator.validate(password);
      } catch (PasswordMaxSizeException e) {
        super.sendError(HttpServletResponse.SC_REQUEST_URI_TOO_LONG, e);
        return;
      } catch (PasswordValidationException e) {
        super.sendResponse(new Response(Response.Status.ERROR, Response.Type.CREATE, e.getMessage()));
        return;
      }

      User user = UserManager.getInstance().getByEMail(address, site);
      if (user == null) {
        Person person = PeopleManager.getInstance().create(connection, name, lastName, owner);
        PeopleManager.getInstance().addEMailAddress(person, address);
      } else {
        try {
          CustomerService.sendRegistrationRetryAlert(site, address);
        } catch (MailServerException | IOException su) {
          sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, su.getMessage());
          return;
        }
        sendResponse(new Response(Response.Status.ERROR, Response.Type.CREATE, "user.already.added"));
        return;
      }
      UserManager.getInstance().set(site, address, password);
      sendResponse(new Response(Response.Status.OK, Response.Type.CREATE, "user.added"));
    } catch (EMailAddressNotExistException e) {
      super.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, e);
    } catch (SQLException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, e.getMessage());
    } catch (JSONParseException e) {
      super.sendError(HttpServletResponse.SC_BAD_REQUEST, e);
    }
  }
}
