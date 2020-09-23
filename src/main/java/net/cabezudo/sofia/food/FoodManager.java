package net.cabezudo.sofia.food;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.food.helpers.AllergensHelper;
import net.cabezudo.sofia.food.helpers.MenuHelper;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.restaurants.RestaurantManager;
import net.cabezudo.sofia.restaurants.RestaurantsTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.03
 */
public class FoodManager {

  private static final FoodManager INSTANCE = new FoodManager();

  private FoodManager() {
    // Nothing to do here
  }

  public static FoodManager getInstance() {
    return INSTANCE;
  }

  public Categories getScheduleByRestaurantId(int id) throws SQLException {
    try ( Connection connection = Database.getConnection(RestaurantsTable.DATABASE)) {
      return getScheduleByRestaurantId(connection, id);
    }
  }

  private Categories getScheduleByRestaurantId(Connection connection, int id) throws SQLException {
    Categories scheduleByCategory = RestaurantManager.getInstance().getScheduleByRestaurantId(connection, id);
    return scheduleByCategory;
  }

  public Menu getMenuByRestaurantId(int id) throws SQLException {
    try ( Connection connection = Database.getConnection(RestaurantsTable.DATABASE)) {
      return getMenuByRestaurantId(connection, id);
    }
  }

  private Menu getMenuByRestaurantId(Connection connection, int id) throws SQLException {
    Categories scheduleByCategory = RestaurantManager.getInstance().getScheduleByRestaurantId(connection, id);

    String query
            = "SELECT "
            + "c.id AS categoryId, c.name AS categoryName, "
            + "dg.id AS dishGroupId, dg.name AS dishGroupName, "
            + "d.id AS dishId, d.name AS dishName, description, imageName, calories, currencyCode, cost "
            + "FROM categories AS c "
            + "LEFT JOIN dishGroups AS dg ON c.id = dg.category "
            + "LEFT JOIN dishes AS d ON dg.id = d.dishGroup "
            + "WHERE c.restaurant = ?";

    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setInt(1, id);
      Logger.fine(ps);
      rs = ps.executeQuery();

      MenuHelper menuHelper = new MenuHelper();

      while (rs.next()) {
        int categoryId = rs.getInt("categoryId");
        String categoryName = rs.getString("categoryName");
        int dishGroupId = rs.getInt("dishGroupId");
        String dishGroupName = rs.getString("dishGroupName");
        int dishId = rs.getInt("dishId");
        String dishName = rs.getString("dishName");
        String description = rs.getString("description");
        String imageName = rs.getString("imageName");
        AllergensHelper allergens = new AllergensHelper();
        int calories = rs.getInt("calories");
        String currencyCode = rs.getString("currencyCode");
        BigDecimal cost = rs.getBigDecimal("cost");

        menuHelper.addCategory(categoryId, categoryName, scheduleByCategory.getSchedule(categoryId));
        menuHelper.addDishGroup(dishGroupId, categoryId, dishGroupName);
        menuHelper.addDish(categoryId, dishId, dishGroupId, dishName, description, imageName, allergens, calories, currencyCode, cost);
      }

      return new Menu(menuHelper);
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
