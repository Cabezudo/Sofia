package net.cabezudo.sofia.food;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.money.Money;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.restaurants.RestaurantsTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.31
 */
public class DishManager {

  private static final DishManager INSTANCE = new DishManager();

  private DishManager() {
    // Nothing to do here
  }

  public static DishManager getInstance() {
    return INSTANCE;
  }

  public Dish add(DishGroup dishGroup, String name, String description, String imageName, Integer calories, Money price)
          throws SQLException {
    try ( Connection connection = Database.getConnection(RestaurantsTable.DATABASE)) {
      return add(connection, dishGroup, name, description, imageName, calories, price);
    }
  }

  public Dish add(Connection connection, DishGroup dishGroup, String name, String description, String imageName, Integer calories, Money price)
          throws SQLException {
    String query
            = "INSERT INTO " + DishTable.DATABASE + "." + DishTable.NAME + " "
            + "(`dishGroup`, `name`, `description`, `imageName`, `calories`, `currencyCode`, `cost`) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      ps.setInt(1, dishGroup.getId());
      ps.setString(2, name);
      ps.setString(3, description);
      ps.setString(4, imageName);
      ps.setObject(5, calories);
      ps.setString(6, price.getCurrency().getCurrencyCode());
      ps.setBigDecimal(7, price.getCost());

      Logger.fine(ps);
      ps.executeUpdate();

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new Dish(id, name, description, imageName, new Allergens(), calories, price);
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
