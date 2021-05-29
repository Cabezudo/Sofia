package net.cabezudo.sofia.core.webusers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.currency.CurrenciesTable;
import net.cabezudo.sofia.core.currency.Currency;
import net.cabezudo.sofia.core.currency.CurrencyManager;
import net.cabezudo.sofia.core.currency.InvalidCurrencyCodeException;
import net.cabezudo.sofia.core.database.sql.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.languages.InvalidTwoLettersCodeException;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.languages.LanguageManager;
import net.cabezudo.sofia.core.languages.LanguagesTable;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.13
 */
public class WebUserDataManager {

  private static final int INITIAL_FAIL_LOGIN_RESPONSE_TIME = 1000;

  private static final WebUserDataManager INSTANCE = new WebUserDataManager();

  private WebUserDataManager() {
    // Only for protect the instanciation
  }

  public static WebUserDataManager getInstance() {
    return INSTANCE;
  }

  public synchronized WebUserData add(HttpServletRequest request) throws ClusterException {
    HttpSession session = request.getSession();
    String sessionId = session.getId();

    try (Connection connection = Database.getConnection()) {
      return insert(connection, request);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public synchronized WebUserData get(HttpServletRequest request) throws ClusterException {
    HttpSession session = request.getSession();
    String sessionId = session.getId();

    try (Connection connection = Database.getConnection()) {
      return get(connection, sessionId);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public WebUserData resetFailLoginResponseTime(WebUserData webUserData) throws ClusterException {
    webUserData = webUserData.setLoginResponseTime(INITIAL_FAIL_LOGIN_RESPONSE_TIME);
    return webUserData;
  }

  public WebUserData incrementFailLoginResponseTime(WebUserData webUserData) throws ClusterException {
    webUserData = webUserData.setLoginResponseTime(webUserData.getFailLoginResponseTime() * 2);
    return webUserData;
  }

  public WebUserData get(String sessionId) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, sessionId);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public WebUserData get(Connection connection, String sessionId) throws ClusterException {
    ResultSet rs = null;
    String query
            = "SELECT "
            + "w.id AS WebUserDataId, `failLoginResponseTime`, "
            + "cl.id AS countryLanguageId, cl.twoLettersCode AS countryLanguageTwoLettersCode, "
            + "al.id AS actualLanguageId, al.twoLettersCode AS actualLanguageTwoLettersCode, "
            + "c.id AS actualCurrencyId, c.currencyCode AS actualCurrencyCode, "
            + "`user` "
            + "FROM " + WebUserDataTable.DATABASE_NAME + "." + WebUserDataTable.NAME + " AS w "
            + "LEFT JOIN " + LanguagesTable.DATABASE_NAME + "." + LanguagesTable.NAME + " AS cl ON w.countryLanguage = cl.id "
            + "LEFT JOIN " + LanguagesTable.DATABASE_NAME + "." + LanguagesTable.NAME + " AS al ON w.actualLanguage = al.id "
            + "LEFT JOIN " + CurrenciesTable.DATABASE_NAME + "." + CurrenciesTable.NAME + " AS c ON w.actualCurrency = c.id "
            + "WHERE sessionId = ?";
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, sessionId);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        Logger.fine("Client data FOUND using " + sessionId + ".");
        int id = rs.getInt("WebUserDataId");
        long failLoginResponseTime = rs.getLong("failLoginResponseTime");
        Language actualLanguage = new Language(rs.getInt("actualLanguageId"), rs.getString("actualLanguageTwoLettersCode"));
        Language countryLanguage = new Language(rs.getInt("countryLanguageId"), rs.getString("countryLanguageTwoLettersCode"));
        Currency actualCurrency = new Currency(rs.getInt("actualCurrencyId"), rs.getString("actualCurrencyCode"));
        int userId = rs.getInt("user");
        return new WebUserData(id, sessionId, failLoginResponseTime, countryLanguage, actualLanguage, actualCurrency, userId);
      }
      Logger.fine("Client data NOT FOUND using " + sessionId + ".");
      return null;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public WebUserData get(int id) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, id);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public WebUserData get(Connection connection, int id) throws ClusterException {
    ResultSet rs = null;
    String query
            = "SELECT "
            + "sessionId, `failLoginResponseTime`, "
            + "cl.id AS countryLanguageId, cl.twoLettersCode AS countryLanguageTwoLettersCode, "
            + "al.id AS actualLanguageId, al.twoLettersCode AS actualLanguageTwoLettersCode, "
            + "c.id AS actualCurrencyId, c.currencyCode AS actualCurrencyCode, "
            + "`user` "
            + "FROM " + WebUserDataTable.DATABASE_NAME + "." + WebUserDataTable.NAME + " AS w "
            + "LEFT JOIN " + LanguagesTable.DATABASE_NAME + "." + LanguagesTable.NAME + " AS cl ON w.countryLanguage = cl.id "
            + "LEFT JOIN " + LanguagesTable.DATABASE_NAME + "." + LanguagesTable.NAME + " AS al ON w.actualLanguage = al.id "
            + "LEFT JOIN " + CurrenciesTable.DATABASE_NAME + "." + CurrenciesTable.NAME + " AS c ON w.actualCurrency = c.id "
            + "WHERE w.id = ?";
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, id);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        Logger.fine("Client data FOUND using " + id + ".");
        String sessionId = rs.getString("sessionId");
        long failLoginResponseTime = rs.getLong("failLoginResponseTime");
        int countryLanguageId = rs.getInt("countryLanguageId");
        String countryLanguageTwoLettersCode = rs.getString("countryLanguageTwoLettersCode");
        Language countryLanguage = new Language(countryLanguageId, countryLanguageTwoLettersCode);
        int actualLanguageId = rs.getInt("actualLanguageId");
        String actualLanguageTwoLettersCode = rs.getString("actualLanguageTwoLettersCode");
        Language actualLanguage = new Language(actualLanguageId, actualLanguageTwoLettersCode);
        int actualCurrencyId = rs.getInt("actualLanguageId");
        String actualCurrencyCode = rs.getString("actualLanguageTwoLettersCode");
        Currency actualCurrency = new Currency(actualCurrencyId, actualCurrencyCode);
        int userId = rs.getInt("user");
        return new WebUserData(id, sessionId, failLoginResponseTime, countryLanguage, actualLanguage, actualCurrency, userId);
      }
      Logger.fine("Client data NOT FOUND using " + id + ".");
      return null;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  private WebUserData insert(Connection connection, HttpServletRequest request) throws SQLException, ClusterException {
    ResultSet rs = null;
    String query
            = "INSERT INTO " + WebUserDataTable.DATABASE_NAME + "." + WebUserDataTable.NAME + " "
            + "(`sessionId`, `failLoginResponseTime`, `countryLanguage`, `actualLanguage`, `actualCurrency`) "
            + "VALUES (?, ?, ?, ?, ?)";
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      String sessionId = request.getSession().getId();
      ps.setString(1, sessionId);
      // TODO take the default values from configuration database for the site
      ps.setLong(2, INITIAL_FAIL_LOGIN_RESPONSE_TIME);
      try {
        // TODO Detect the country using the localization. IP and Web localization
        // TODO use the default country language
        Language language = LanguageManager.getInstance().get(request);
        ps.setInt(3, language.getId());
        ps.setInt(4, language.getId());
        // TODO Get the country currency
        Currency mexicanPeso = CurrencyManager.getInstance().get("MXN");
        ps.setInt(5, mexicanPeso.getId());
        ClusterManager.getInstance().executeUpdate(ps);
        rs = ps.getGeneratedKeys();
        if (rs.next()) {
          int id = rs.getInt(1);
          return new WebUserData(id, sessionId, INITIAL_FAIL_LOGIN_RESPONSE_TIME, language, language, mexicanPeso);
        }
      } catch (InvalidTwoLettersCodeException | InvalidCurrencyCodeException e) {
        throw new SofiaRuntimeException(e);
      }
      throw new SofiaRuntimeException("Can't get the generated key");
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public void insert(HttpServletRequest request) throws InvalidTwoLettersCodeException, ClusterException {
    try (Connection connection = Database.getConnection()) {
      insert(connection, request);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public void update(WebUserData webUserData) throws ClusterException {
    String query
            = "UPDATE " + WebUserDataTable.DATABASE_NAME + "." + WebUserDataTable.NAME + " "
            + "SET `failLoginResponseTime` = ?, `countryLanguage` = ?, `actualLanguage` = ? , `actualCurrency` = ? "
            + "WHERE sessionId = ?";
    try (Connection connection = Database.getConnection(); PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setLong(1, webUserData.getFailLoginResponseTime());
      ps.setInt(2, webUserData.getCountryLanguage().getId());
      ps.setInt(3, webUserData.getActualLanguage().getId());
      ps.setInt(4, webUserData.getActualCurrency().getId());
      ps.setString(5, webUserData.getSessionId());

      ClusterManager.getInstance().executeUpdate(ps);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }
}
