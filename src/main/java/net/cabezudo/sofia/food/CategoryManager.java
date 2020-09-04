package net.cabezudo.sofia.food;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.restaurants.Restaurant;
import net.cabezudo.sofia.restaurants.Schedule;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.31
 */
public class CategoryManager {

  private static final CategoryManager INSTANCE = new CategoryManager();

  public static CategoryManager getInstance() {
    return INSTANCE;
  }

  public Category add(Restaurant restaurant, String name) throws SQLException {
    try ( Connection connection = Database.getConnection(CategoriesTable.DATABASE)) {
      return add(connection, restaurant, name);
    }
  }

  public Category add(Connection connection, Restaurant restaurant, String name) throws SQLException {
    String query = "INSERT INTO " + CategoriesTable.DATABASE + "." + CategoriesTable.NAME + " (restaurant, name) VALUES (?, ?)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      ps.setInt(1, restaurant.getId());
      ps.setString(2, name);

      Logger.fine(ps);
      ps.executeUpdate();

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new Category(id, restaurant, name, new Schedule());
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

  public Category get(int id) throws SQLException {
    try ( Connection connection = Database.getConnection(CategoriesTable.DATABASE)) {
      return get(connection, id);
    }
  }

  public Category get(Connection connection, int id) throws SQLException {
    String query = "SELECT id, name FROM " + CategoriesTable.DATABASE + "." + CategoriesTable.NAME + " WHERE id = ?";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setInt(1, id);
      Logger.fine(ps);
      rs = ps.executeQuery();
      if (rs.next()) {
        return new Category(rs.getInt("id"), null, rs.getString("name"), null);
      }
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
    return null;
  }

  public Category get(String name) throws SQLException {
    try ( Connection connection = Database.getConnection(CategoriesTable.DATABASE)) {
      return get(connection, name);
    }
  }

  public Category get(Connection connection, String name) throws SQLException {
    String query = "SELECT id, name FROM " + CategoriesTable.DATABASE + "." + CategoriesTable.NAME + " WHERE name = ?";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setString(1, name);
      Logger.fine(ps);
      rs = ps.executeQuery();
      if (rs.next()) {
        return new Category(rs.getInt("id"), null, rs.getString("name"), null);
      }
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
    return null;
  }
}
