package net.cabezudo.sofia.core.languages;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.database.sql.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.11.23
 */
public class LanguageManager {

  private static final LanguageManager INSTANCE = new LanguageManager();

  private LanguageManager() {
    // Protect the singleton
  }

  public static LanguageManager getInstance() {
    return INSTANCE;
  }

  public Language get(String twoLettersCode) throws InvalidTwoLettersCodeException, ClusterException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, twoLettersCode);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Language get(Connection connection, String twoLettersCode) throws InvalidTwoLettersCodeException, ClusterException {
    String query
            = "SELECT id, twoLettersCode "
            + "FROM " + LanguagesTable.DATABASE_NAME + "." + LanguagesTable.NAME + " "
            + "WHERE twoLettersCode = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, twoLettersCode);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        return new Language(rs.getInt("id"), rs.getString("twoLettersCode"));
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    throw new InvalidTwoLettersCodeException(twoLettersCode);
  }

  public Language add(Connection connection, String twoLettersCode) throws ClusterException {
    String query = "INSERT INTO " + LanguagesTable.DATABASE_NAME + "." + LanguagesTable.NAME + " (twoLettersCode) VALUES (?)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ps.setString(1, twoLettersCode);
      ClusterManager.getInstance().executeUpdate(ps);
      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new Language(id, twoLettersCode);
      }
      throw new SofiaRuntimeException("Can't get the generated key");
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public Language get(HttpServletRequest request) throws InvalidTwoLettersCodeException, ClusterException {
    Enumeration<Locale> locales = request.getLocales();
    while (locales.hasMoreElements()) {
      Locale locale = locales.nextElement();
      return get(locale.getLanguage());
    }
    return get("en");
  }
}
