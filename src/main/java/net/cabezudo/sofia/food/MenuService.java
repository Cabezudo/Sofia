package net.cabezudo.sofia.food;

import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLToken;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.ws.servlet.services.QueryParameters;
import net.cabezudo.sofia.core.ws.servlet.services.Service;
import net.cabezudo.sofia.restaurants.*;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.18
 */
public class MenuService extends Service {

  public MenuService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void execute() throws ServletException {
    URLToken token = tokens.getValue("path");
    String path = token.toString();

    QueryParameters queryParameters = getQueryParmeters();

    String stringOffset = queryParameters.get("timeZoneOffset");
    int offset;
    try {
      offset = Integer.parseInt(stringOffset);
    } catch (NumberFormatException e) {
      sendError(400, "Invalid timeZoneOffset value");
      return;
    }

    try {
      Restaurant restaurant = RestaurantManager.getInstance().get(path, offset);
      Menu menu = FoodManager.getInstance().getMenuByRestaurantId(restaurant.getId());
      Categories categories = menu.getCategories();
      CategoryHoursList categoriesHours = new CategoryHoursList(restaurant, categories);

      JSONObject jsonObject = new JSONObject();
      jsonObject.add(new JSONPair("restaurant", restaurant.toJSONTree()));
      JSONObject jsonHours = new JSONObject();
      jsonHours.add(new JSONPair("isOpen", restaurant.getBusinessHours().isOpen()));
      jsonHours.add(new JSONPair("categories", categoriesHours.toJSONTree()));
      jsonObject.add(new JSONPair("hours", jsonHours));
      jsonObject.add(new JSONPair("menu", menu.toJSONTree()));
      out.print(jsonObject.toJSON());
    } catch (SQLException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, e.getMessage());
    }
  }

}
