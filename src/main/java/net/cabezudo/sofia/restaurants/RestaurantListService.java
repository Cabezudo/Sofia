package net.cabezudo.sofia.restaurants;

import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.ws.servlet.services.QueryParameters;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.18
 */
public class RestaurantListService extends Service {

  public RestaurantListService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void execute() throws ServletException {
    String queryString = request.getQueryString();
    if (queryString != null) {
      // TODO agregar los filtros, el orden y demas
    }

    QueryParameters queryParameters = getQueryParmeters();
    String stringTimezoneOffset = queryParameters.get("timezoneOffset");
    int timezoneOffset;
    try {
      timezoneOffset = Integer.parseInt(stringTimezoneOffset);
    } catch (NumberFormatException e) {
      sendError(400, "Invalid timezoneOffset value");
      return;
    }

    try {
      RestaurantList list = RestaurantManager.getInstance().list();
      list.calculateFor(300);
      out.print(list.toJSON());
    } catch (SQLException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, e.getMessage());
    }
  }

}
