package net.cabezudo.sofia.core.ws.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.clients.DetailClientsService;
import net.cabezudo.sofia.clients.ListClientsService;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.passwords.PasswordPairValidatorService;
import net.cabezudo.sofia.core.passwords.PasswordValidatorService;
import net.cabezudo.sofia.core.passwords.RecoverPasswordService;
import net.cabezudo.sofia.core.passwords.SetPasswordService;
import net.cabezudo.sofia.core.sites.SiteHostameListService;
import net.cabezudo.sofia.core.sites.SiteHostnameNameValidationService;
import net.cabezudo.sofia.core.sites.SiteListService;
import net.cabezudo.sofia.core.sites.SiteModifyDomainNameService;
import net.cabezudo.sofia.core.sites.SiteModifyService;
import net.cabezudo.sofia.core.sites.SiteNameValidationService;
import net.cabezudo.sofia.core.sites.SiteService;
import net.cabezudo.sofia.core.sites.SiteVersionService;
import net.cabezudo.sofia.core.users.AddUserService;
import net.cabezudo.sofia.core.users.ListUsersService;
import net.cabezudo.sofia.core.users.autentication.AuthenticatedService;
import net.cabezudo.sofia.core.users.autentication.LoginService;
import net.cabezudo.sofia.core.ws.parser.URLPathTokenizer;
import net.cabezudo.sofia.core.ws.parser.tokens.Tokens;
import net.cabezudo.sofia.emails.EMailValidatorService;
import net.cabezudo.sofia.people.ListPeopleService;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.13
 */
public class WebServices extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    response.setHeader("Content-Type", "application/json; charset=utf-8");

    String uri = URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8);

    String serverName = request.getServerName();
    if (serverName.startsWith("api.")) {
      uri = "/api" + uri;
    }

    Logger.debug("GET request for: %s.", uri);
    Tokens tokens = URLPathTokenizer.tokenize(uri);

    if (tokens.match("/api/v1/sites")) {
      new SiteListService(request, response, tokens).execute();
      return;
    }

    if (tokens.match("/api/v1/sites/{siteId}")) {
      new SiteService(request, response, tokens).execute();
      return;
    }

    if (tokens.match("/api/v1/sites/{siteId}/names/{name}/validate")) {
      new SiteNameValidationService(request, response, tokens).execute();
      return;
    }

    if (tokens.match("/api/v1/sites/{siteId}/versions/{version}/validate")) {
      new SiteVersionService(request, response, tokens).execute();
      return;
    }

    if (tokens.match("/api/v1/sites/{siteId}/hosts")) {
      new SiteHostameListService(request, response, tokens).execute();
      return;
    }

    if (tokens.match("/api/v1/sites/{siteId}/hosts/{hostname}/names/{name}/validate")) {
      new SiteHostnameNameValidationService(request, response, tokens).execute();
      return;
    }

    if (tokens.match("/api/v1/mails/{email}/validate")) {
      new EMailValidatorService(request, response, tokens).execute();
      return;
    }

    // GET /api/v1/users/logged
    if (tokens.match("/api/v1/users/logged")) {
      new AuthenticatedService(request, response).execute();
      return;
    }

    // GET /api/v1/users/{email}/password/recover
    if (tokens.match("/api/v1/users/{email}/password/recover")) {
      new RecoverPasswordService(request, response, tokens).execute();
      return;
    }

    // GET /api/v1/users
    if (tokens.match("/api/v1/users")) {
      new ListUsersService(request, response).execute();
      return;
    }

    // List the persons. GET /api/v1/persons
    if (tokens.match("/api/v1/persons")) {
      new ListPeopleService(request, response).execute();
      return;
    }

    // List the clients. GET /api/v1/clients?sort=+name,-lastName&fields=name,lastName&offset=10&limit=50
    if (tokens.match("/api/v1/clients")) {
      new ListClientsService(request, response).execute();
      return;
    }

    // List the clients. GET /api/v1/clients?sort=+name,-lastName&fields=name,lastName&offset=10&limit=50
    if (tokens.match("/api/v1/clients/{clientId}")) {
      new DetailClientsService(request, response, tokens).execute();
      return;
    }

    // List the enterprises. GET /api/v1/enterprises
    if (tokens.match("/api/v1/enterprises")) {
    }

    // List the asociated person to this enterprise. GET /api/v1/enterprise/{enterpriseId}/person
    if (tokens.match("/api/v1/enterprise/{enterpriseId}/person")) {
    }

    // List the restaurants. GET /api/v1/restaurants
    if (tokens.match("/api/v1/restaurants")) {
    }

    // List the asociated person to this restaurant. GET /api/v1/restaurant/{restaurantId}/person
    if (tokens.match("/api/v1/restaurant/{restaurantId}/person")) {
    }

    // List the dishes for a restaurant. GET /api/v1/restaurant/{restaurantId}/dishes
    if (tokens.match("/api/v1/restaurant/{restaurantId}/dishes")) {
    }

    // List courses for a restaurant. GET /api/v1/restaurant/{restaurantId}/courses
    if (tokens.match("/api/v1/restaurant/{restaurantId}/courses")) {
    }

    // List courses for a day for a restaurant. GET /api/v1/restaurant/{restaurantId}/year/{year}/month/{month}/day/{day}/courses
    if (tokens.match("/api/v1/restaurant/{restaurantId}/year/{year}/month/{month}/day/{day}/courses")) {
    }

    // List ingredients for a restaurant. GET /api/v1/restaurant/{restaurantId}/ingredients
    if (tokens.match("/api/v1/restaurant/{restaurantId}/ingredients")) {
    }
    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Searching " + uri);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String uri = request.getRequestURI();

    Logger.debug("POST request for: %s.", uri);
    Tokens tokens = URLPathTokenizer.tokenize(uri);

    // Validate the format for a String with a password. GET /api/v1/password/validate/
    if (tokens.match("/api/v1/password/validate")) {
      new PasswordValidatorService(request, response).execute();
      return;
    }

    // Validate the format for a pair of String with a password. GET /api/v1/password/pair/validate/
    if (tokens.match("/api/v1/password/pair/validate")) {
      new PasswordPairValidatorService(request, response).execute();
      return;
    }

    // POST /api/v1/users/login
    if (tokens.match("/api/v1/users/login")) {
      new LoginService(request, response).execute();
      return;
    }

    // POST /api/v1/users/{hash}/password
    if (tokens.match("/api/v1/users/{hash}/password")) {
      new SetPasswordService(request, response, tokens).execute();
      return;
    }

    // Registre a user without validate it. /api/v1/users POST (create user)
    if (tokens.match("/api/v1/users")) {
      new AddUserService(request, response).execute();
      return;
    }

    // Create a person. POST /api/v1/person (create)
    if (tokens.match("/api/v1/person")) {
    }

    // Create a enterprise. POST /api/v1/enterprise/
    if (tokens.match("/api/v1/enterprise/")) {
    }

    // Asociate to the enterprise one person. POST /api/v1/enterprise/{enterpriseId}/person/{personId}
    if (tokens.match("/api/v1/enterprise/{enterpriseId}/person/{personId}")) {
    }

    // Create a restaurant. POST /api/v1/restaurant
    if (tokens.match("/api/v1/restaurant")) {
    }

    // Asociate to the restaurant one person. POST /api/v1/restaurant/{restaurantId}/person/{personId}
    if (tokens.match("/api/v1/restaurant/{restaurantId}/person/{personId}")) {
    }

    // Create a dish for a restaurant. POST /api/v1/restaurant/{restaurantId}/dish
    if (tokens.match("/api/v1/restaurant/{restaurantId}/dish")) {
    }

    // Create a restaurant course. POST /api/v1/restaurant/{restaurantId}/year/{year}/month/{month}/day/{day}/course
    if (tokens.match("/api/v1/restaurant/{restaurantId}/year/{year}/month/{month}/day/{day}/course")) {
    }

    // Assign course to a day. POST /api/v1/restaurant/{restaurantId}/year/{year}/month/{month}/day/{day}/course/{courseId}
    if (tokens.match("/api/v1/restaurant/{restaurantId}/year/{year}/month/{month}/day/{day}/course/{courseId}")) {
    }

    // Assign dish to restaurant course. POST /api/v1/course/{courseId}/dish/{dishId}
    if (tokens.match("/api/v1/course/{courseId}/dish/{dishId}")) {
    }

    // Create a ingredient for a restaurant. POST /api/v1/restaurant/{restaurantId}/ingredient
    if (tokens.match("/api/v1/restaurant/{restaurantId}/ingredient")) {
    }
    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Searching " + uri);
  }

  @Override
  protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String uri = request.getRequestURI();
    Tokens tokens = URLPathTokenizer.tokenize(uri);

    // Update data for a site. PUT /api/v1/sites/{siteId}
    if (tokens.match("/api/v1/sites/{siteId}")) {
      new SiteModifyService(request, response, tokens).execute();
      return;
    }

    // Update a domain for a site. PUT /api/v1/sites/{siteId}/hosts/{hostId}
    if (tokens.match("/api/v1/sites/{siteId}/hosts/{hostId}")) {
      new SiteModifyDomainNameService(request, response, tokens).execute();
      return;
    }

    // Update the data for a person. PUT /api/v1/person/{personId}
    if (tokens.match("/api/v1/person/{personId}")) {
    }

    // Update a enterprise. PUT /api/v1/enterprise/{enterpriseId}
    if (tokens.match("/api/v1/enterprise/{enterpriseId}")) {
    }

    // Update a restaurant. PUT /api/v1/restaurant/{restaurantId}
    if (tokens.match("/api/v1/restaurant/{restaurantId}")) {
    }

    // Update a restaurant dish. PUT /api/v1/dish/{dishId}
    if (tokens.match("/api/v1/dish/{dishId}")) {
    }

    // Update a restaurant course. PUT /api/v1/course/{courseId}
    if (tokens.match("/api/v1/course/{courseId}")) {
    }

    // Update a ingredient for a restaurant. PUT /api/v1/restaurant/{restaurantId}/ingredient/{ingredientId}
    if (tokens.match("/api/v1/restaurant/{restaurantId}/ingredient/{ingredientId}")) {
    }

    // Publish day courses. PUT /api/v1/year/{year}/month/{month}/day/{day}/courses/publish
    if (tokens.match("/api/v1/year/{year}/month/{month}/day/{day}/courses/publish")) {
    }

    // Unpublish day courses. PUT /api/v1/year/{year}/month/{month}/day/{day}/courses/unpublish
    if (tokens.match("/api/v1/year/{year}/month/{month}/day/{day}/courses/unpublish")) {
    }
    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Searching " + uri);
  }

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String uri = request.getRequestURI();
    Tokens tokens = URLPathTokenizer.tokenize(uri);

    // DELETE /api/v1/users/{userId}
    if (tokens.match("/api/v1/users/{userId}")) {
    }

    // Delete a person. DELETE /api/v1/person/{personId}
    if (tokens.match("/api/v1/person/{personId}")) {
    }

    // Delete the enterprise. DELETE /api/v1/enterprise/{enterpriseId}
    if (tokens.match("/api/v1/enterprise/{enterpriseId}")) {
    }

    // Delete the asociacion between the enterprise and the person. DELETE /api/v1/enterprise/{enterpriseId}/person/{personId}
    if (tokens.match("/api/v1/enterprise/{enterpriseId}/person/{personId}")) {
    }

    // Delete the restaurant. DELETE /api/v1/restaurant/{restaurantId}
    if (tokens.match("/api/v1/restaurant/{restaurantId}")) {
    }

    // Delete the asociacion between the restaurant and the person. DELETE /api/v1/restaurant/{restaurantId}/person/{personId}
    if (tokens.match("/api/v1/restaurant/{restaurantId}/person/{personId}")) {
    }

    // Delete a dish for a restaurant. DELETE /api/v1/restaurant/{restaurantId}/dish/{dishId}
    if (tokens.match("/api/v1/restaurant/{restaurantId}/dish/{dishId}")) {
    }

    // Delete a restaurant course. DELETE /api/v1/course/{courseId}
    if (tokens.match("/api/v1/course/{courseId}")) {
    }

    // Delete course for a day. DELETE /api/v1/course/{courseId}
    if (tokens.match("/api/v1/course/{courseId}")) {
    }

    // Delete dish to restaurant course. DELETE /api/v1/course/{courseId}/dish/{dishId}
    if (tokens.match("/api/v1/course/{courseId}/dish/{dishId}")) {
    }

    // Delete a ingredient for a restaurant. DELETE /api/v1/ingredient/{ingredientId}
    if (tokens.match("/api/v1/ingredient/{ingredientId}")) {
    }
    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Searching " + uri);
  }
}
