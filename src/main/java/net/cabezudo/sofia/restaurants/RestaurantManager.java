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
import net.cabezudo.sofia.core.schedule.AbstractTime;
import net.cabezudo.sofia.core.schedule.TimeFactory;
import net.cabezudo.sofia.core.schedule.TimeType;
import net.cabezudo.sofia.core.schedule.TimeTypesTable;
import net.cabezudo.sofia.core.schedule.TimesTable;
import net.cabezudo.sofia.food.Categories;
import net.cabezudo.sofia.food.CategoriesTable;
import net.cabezudo.sofia.food.helpers.CategoriesHelper;
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

  private String getRestaurantQuery() {
    return "SELECT "
            + "  r.id, `subdomain`, `imageName`, r.name AS name, `location`, `typeId`, t.name as typeName, `priceRange`, "
            + "  `currencyCode`, `shippingCost`, `minDeliveryTime`, `maxDeliveryTime`, `score`, `numberOfVotes`, "
            + "`longitude`, `latitude` "
            + "FROM " + RestaurantsTable.DATABASE + "." + RestaurantsTable.NAME + " AS r "
            + "LEFT JOIN " + RestaurantTypesTable.DATABASE + "." + RestaurantTypesTable.NAME + " AS t ON r.typeId = t.id";
  }

  public Restaurant get(String path) throws SQLException {
    try ( Connection connection = Database.getConnection(RestaurantsTable.DATABASE)) {
      return get(connection, path);
    }
  }

  public Restaurant get(Connection connection, String path) throws SQLException {
    String query = getRestaurantQuery() + " WHERE subdomain = ?";

    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setString(1, path);
      Logger.fine(ps);
      rs = ps.executeQuery();

      if (rs.next()) {
        return createRestaurant(rs);
      }
      return null;
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
  }

  public RestaurantList list(Connection connection) throws SQLException {
    String query = getRestaurantQuery();

    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      Logger.fine(ps);
      rs = ps.executeQuery();

      RestaurantList list = new RestaurantList(0);

      while (rs.next()) {
        Restaurant restaurant = createRestaurant(rs);
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

  public Restaurant add(
          String subdomain, String imageName, String name, String location, RestaurantType type, int priceRange, Currency currency, Money shippingCost,
          DeliveryRange deliveryRange
  ) throws SQLException {
    try ( Connection connection = Database.getConnection(RestaurantsTable.DATABASE)) {
      return add(connection, subdomain, imageName, name, location, type, priceRange, currency, shippingCost, deliveryRange);
    }
  }

  public Restaurant add(
          Connection connection, String subdomain, String imageName, String name, String location, RestaurantType type, int priceRange, Currency currency,
          Money shippingCost, DeliveryRange deliveryRange)
          throws SQLException {
    String query
            = "INSERT INTO " + RestaurantsTable.DATABASE + "." + RestaurantsTable.NAME + " "
            + "("
            + "`subdomain`, `imageName`, `name`, `location`, `typeId`, `priceRange`, "
            + "`currencyCode`, `shippingCost`, `minDeliveryTime`, `maxDeliveryTime`"
            + ") "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

      int minDeliveryTime = deliveryRange.getMin();
      int maxDeliveryTime = deliveryRange.getMax();

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

      Logger.fine(ps);
      ps.executeUpdate();

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new Restaurant(id, subdomain, imageName, name, location, type, priceRange, currency, shippingCost, deliveryRange, null, null, null, null);
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

  private Restaurant createRestaurant(ResultSet rs) throws SQLException {
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
    BigDecimal cost = rs.getBigDecimal("shippingCost");
    Money shippingCost = new Money(currency, cost);
    int minDeliveryTime = rs.getInt("minDeliveryTime");
    int maxDeliveryTime = rs.getInt("maxDeliveryTime");
    DeliveryRange deliveryRange = new DeliveryRange(minDeliveryTime, maxDeliveryTime);
    int score = rs.getInt("score");
    int numberOfVotes = rs.getInt("numberOfVotes");
    BigDecimal longitude = rs.getBigDecimal("longitude");
    BigDecimal latitude = rs.getBigDecimal("latitude");

    return new Restaurant(
            id, subdomain, imageName, name, location, type, priceRange, currency, shippingCost, deliveryRange, score, numberOfVotes, latitude, longitude);

  }

  public Categories getScheduleByRestaurantId(Connection connection, int id) throws SQLException {
    String query
            = "SELECT c.`id` AS categoryId, c.`name` AS categoryName, `type` AS typeId, tt.`name` AS typeName, `index`, `start`, `end` "
            + "FROM " + CategoriesTable.DATABASE + "." + CategoriesTable.NAME + " AS c "
            + "LEFT JOIN " + TimesTable.DATABASE + "." + TimesTable.NAME + " AS t ON c.schedule = t.entry "
            + "LEFT JOIN " + TimeTypesTable.DATABASE + "." + TimeTypesTable.NAME + " as tt ON t.type = tt.id "
            + "WHERE c.restaurant = ?";

    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setInt(1, id);
      Logger.fine(ps);
      rs = ps.executeQuery();

      CategoriesHelper categoriesHelper = new CategoriesHelper();
      while (rs.next()) {
        int categoryId = rs.getInt("categoryId");
        String categoryName = rs.getString("categoryName");
        int typeId = rs.getInt("typeId");
        String typeName = rs.getString("typeName");
        TimeType type = new TimeType(typeId, typeName);
        int index = rs.getInt("index");
        int start = rs.getInt("start");
        int end = rs.getInt("end");
        AbstractTime time = TimeFactory.get(id, type, index, start, end);
        categoriesHelper.add(categoryId, categoryName, time);
      }
      return new Categories(categoriesHelper);
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
