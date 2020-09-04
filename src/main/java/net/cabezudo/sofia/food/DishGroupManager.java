package net.cabezudo.sofia.food;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.02
 */
public class DishGroupManager {

  private static final DishGroupManager INSTANCE = new DishGroupManager();

  public static DishGroupManager getInstance() {
    return INSTANCE;
  }

  public DishGroup add(Category category, String name) throws SQLException {
    try ( Connection connection = Database.getConnection()) {
      return add(connection, category, name);
    }

  }

  public DishGroup add(Connection connection, Category category, String name) throws SQLException {
    String query
            = "INSERT INTO " + DishGroupsTable.DATABASE + "." + DishGroupsTable.NAME + " "
            + "(`category`, `name`) VALUES (?, ?)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      ps.setInt(1, category.getId());
      ps.setString(2, name);

      Logger.fine(ps);
      ps.executeUpdate();

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new DishGroup(id, name, new Dishes());
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
