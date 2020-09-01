package net.cabezudo.sofia.restaurants;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Currency;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.money.Money;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.29
 */
public class RestaurantManager {

  private static final RestaurantManager INSTANCE = new RestaurantManager();

  public static RestaurantManager getInstance() {
    return INSTANCE;
  }

  private RestaurantManager() {
    // Nothing to do here
  }

  public RestaurantList list() throws SQLException {
    try ( Connection connection = Database.getConnection(RestaurantsTable.DATABASE)) {
      return list(connection);
    }
  }

  public RestaurantList list(Connection connection) throws SQLException {
    String query
            = "SELECT "
            + "  r.id, `subdomain`, `imageName`, r.name AS name, `location`, `typeId`, t.name as typeName, `priceRange`, "
            + "  `currencyCode`, `shippingCost`, `minDeliveryTime`, `maxDeliveryTime`, `score` "
            + "FROM " + RestaurantsTable.DATABASE + "." + RestaurantsTable.NAME + " AS r "
            + "LEFT JOIN " + RestaurantTypesTable.DATABASE + "." + RestaurantTypesTable.NAME + " AS t ON r.typeId = t.id";

    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      Logger.fine(ps);
      rs = ps.executeQuery();

      RestaurantList list = new RestaurantList(0);

      while (rs.next()) {
        int id = rs.getInt("id");
        String subdomain = rs.getString("subdomain");
        String imageName = rs.getString("imageName");
        String name = rs.getString("name");
        String location = rs.getString("location");
        int restaurantTypeId = rs.getInt("typeId");
        String restaurantTypeName = rs.getString("typeName");
        RestaurantType type = new RestaurantType(restaurantTypeId, restaurantTypeName);
        int priceRange = rs.getInt("priceRange");
        String currencyCode = rs.getString("currencyCode");
        Currency currency;
        if (currencyCode == null) {
          currency = null;
        } else {
          currency = Currency.getInstance(currencyCode);
        }
        BigDecimal shippingCost = rs.getBigDecimal("shippingCost");
        int minDeliveryTime = rs.getInt("minDeliveryTime");
        int maxDeliveryTime = rs.getInt("maxDeliveryTime");
        int score = rs.getInt("score");

        Restaurant restaurant = new Restaurant(id, subdomain, imageName, name, type, currency);
        restaurant.setLocation(location);
        restaurant.setPriceRange(priceRange);
        restaurant.setShippingCost(new Money(shippingCost, currency));
        restaurant.setMinDeliveryTime(minDeliveryTime);
        restaurant.setMaxDeliveryTime(maxDeliveryTime);
        restaurant.setScore(score);

        list.add(restaurant);
      }
      return list;
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
  }

  public Restaurant add(Restaurant restaurant) throws SQLException {
    try ( Connection connection = Database.getConnection(RestaurantsTable.DATABASE)) {
      return add(connection, restaurant);
    }
  }

  public Restaurant add(Connection connection, Restaurant r) throws SQLException {
    String query
            = "INSERT INTO " + RestaurantsTable.DATABASE + "." + RestaurantsTable.NAME + " "
            + "("
            + "`subdomain`, `imageName`, `name`, `location`, `typeId`, `priceRange`, `currencyCode`, `shippingCost`, `minDeliveryTime`, "
            + "`maxDeliveryTime`, `score`"
            + ") "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

      String subdomain = r.getSubdomain();
      String imageName = r.getImageName();
      String name = r.getName();
      String location = r.getLocation();
      RestaurantType type = r.getType();
      int priceRange = r.getPriceRange();
      Currency currency = r.getCurrency();
      Money shippingCost = r.getShippingCost();
      int minDeliveryTime = r.getMinDeliveryTime();
      int maxDeliveryTime = r.getMaxDeliveryTime();
      int score = r.getScore();

      ps.setString(1, subdomain);
      ps.setString(2, imageName);
      ps.setString(3, name);
      ps.setString(4, location);
      if (type == null) {
        ps.setObject(5, null);
      } else {
        ps.setInt(5, type.getId());
      }
      ps.setInt(6, priceRange);
      ps.setString(7, currency.getCurrencyCode());
      if (shippingCost == null) {
        ps.setBigDecimal(8, null);
      } else {
        ps.setBigDecimal(8, shippingCost.getCost());
      }
      ps.setInt(9, minDeliveryTime);
      ps.setInt(10, maxDeliveryTime);
      ps.setInt(11, score);

      Logger.fine(ps);
      ps.executeUpdate();

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        Restaurant restaurant = new Restaurant(id, subdomain, imageName, name, type, currency);
        restaurant.setLocation(location);
        restaurant.setPriceRange(priceRange);
        restaurant.setShippingCost(shippingCost);
        restaurant.setMinDeliveryTime(minDeliveryTime);
        restaurant.setMaxDeliveryTime(maxDeliveryTime);
        restaurant.setScore(score);

        return restaurant;
      }
      throw new SofiaRuntimeException("Can't get the generated key");
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
  }
}
