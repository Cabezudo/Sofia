package net.cabezudo.sofia.core.languages;

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
 * @version 0.01.00, 2020.11.23
 */
public class LanguageManager {

  private static final LanguageManager INSTANCE = new LanguageManager();

  private LanguageManager() {
    // Nothing to do here. Jus protect the instance
  }

  public static LanguageManager getInstance() {
    return INSTANCE;
  }

  public Language get(String twoLettersCode) throws SQLException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, twoLettersCode);
    }
  }

  public Language get(Connection connection, String twoLettersCode) throws SQLException {
    String query
            = "SELECT id, twoLettersCode "
            + "FROM " + LanguagesTable.DATABASE + "." + LanguagesTable.NAME + " "
            + "WHERE twoLettersCode = ?";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setString(1, twoLettersCode);
      Logger.fine(ps);
      rs = ps.executeQuery();
      if (rs.next()) {
        return new Language(rs.getInt("id"), rs.getString("twoLettersCode"));
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

  public Language add(Connection connection, String twoLettersCode) throws SQLException {
    String query = "INSERT INTO " + LanguagesTable.DATABASE + "." + LanguagesTable.NAME + " (twoLettersCode) VALUES (?)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, twoLettersCode);
      Logger.fine(ps);
      ps.executeUpdate();

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new Language(id, twoLettersCode);
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
