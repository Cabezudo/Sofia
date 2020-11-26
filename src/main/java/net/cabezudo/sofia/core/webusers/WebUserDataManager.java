package net.cabezudo.sofia.core.webusers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.languages.InvalidTwoLettersCodeException;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.languages.LanguageManager;
import net.cabezudo.sofia.core.languages.LanguagesTable;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserManager;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.13
 */
public class WebUserDataManager {

  private static final int INITIAL_FAIL_LOGIN_RESPONSE_TIME = 1000;

  private static WebUserDataManager INSTANCE = new WebUserDataManager();

  private WebUserDataManager() {
    // Only for protect the instanciation
  }

  public static WebUserDataManager getInstance() {
    return INSTANCE;
  }

  public WebUserData createFakeWebUserData() throws SQLException, InvalidTwoLettersCodeException {
    Language spanish = LanguageManager.getInstance().get("es");
    return new WebUserData(1, "fackeSessionId", 0, spanish, spanish);
  }

  public class WebUserData {

    private final int id;
    private final String sessionId;
    private final long failLoginResponseTime;
    private Language countryLanguage;
    private Language actualLanguage;
    private int userId;
    private User user;
    private JSONObject jsonObject;

    private WebUserData(int id, String sessionId, long failLoginResponseTime, Language countryLanguage, Language actualLanguage) {
      this(id, sessionId, failLoginResponseTime, countryLanguage, actualLanguage, 0);
    }

    private WebUserData(int id, String sessionId, long failLoginResponseTime, Language countryLanguage, Language actualLanguage, int userId) {
      this.id = id;
      this.sessionId = sessionId;
      this.failLoginResponseTime = failLoginResponseTime;
      this.countryLanguage = countryLanguage;
      this.actualLanguage = actualLanguage;
      this.userId = userId;
    }

    public int getId() {
      return id;
    }

    public String getSessionId() {
      return sessionId;
    }

    public Language getCountryLanguage() {
      return countryLanguage;
    }

    public Language getActualLanguage() {
      return actualLanguage;
    }

    public long getFailLoginResponseTime() {
      return this.failLoginResponseTime;
    }

    public boolean isLogged() {
      return user != null;
    }

    @Override
    public String toString() {
      if (jsonObject == null) {
        jsonObject = new JSONObject();

        jsonObject.add(new JSONPair("id", id));
        jsonObject.add(new JSONPair("sessionId", sessionId));
        jsonObject.add(new JSONPair("failLoginResponseTime", failLoginResponseTime));
        jsonObject.add(new JSONPair("countryLanguage", countryLanguage.toJSONTree()));
        jsonObject.add(new JSONPair("actualLanguage", actualLanguage.toJSONTree()));
        jsonObject.add(new JSONPair("logged", isLogged()));
        try {
          user = getUser();
        } catch (SQLException e) {
          Logger.warning(e);
          user = null;
        }
        jsonObject.add(new JSONPair("user", user == null ? user : user.toString()));
      }
      return jsonObject.toString();
    }

    private WebUserData setLoginResponseTime(long failLoginResponseTime) {
      jsonObject = null;
      return new WebUserData(id, sessionId, failLoginResponseTime, countryLanguage, actualLanguage, userId);
    }

    public User getUser() throws SQLException {
      if (userId != 0 && user == null) {
        user = UserManager.getInstance().get(userId);
      }
      return user;
    }

    public void setUser(User user) throws SQLException {
      jsonObject = null;
      if (user == null) {
        update("user", 0, this.sessionId);
      } else {
        update("user", user.getId(), this.sessionId);
      }
      this.user = user;
    }

    public void setLanguage(String twoLetterCodeLanguage) throws SQLException, InvalidTwoLettersCodeException {
      Language language = LanguageManager.getInstance().get(twoLetterCodeLanguage);
      this.actualLanguage = language;
      update("actualLanguage", language.getId(), this.sessionId);
    }
  }

  public synchronized WebUserData get(HttpServletRequest request) throws SQLException {
    HttpSession session = request.getSession();
    String sessionId = session.getId();

    try (Connection connection = Database.getConnection()) {
      connection.setAutoCommit(false);
      WebUserData webUserData = get(connection, sessionId);

      if (webUserData == null) {
        webUserData = insert(connection, sessionId);
      }
      connection.commit();
      return webUserData;
    }
  }

  public WebUserData resetFailLoginResponseTime(WebUserData webUserData) throws SQLException {
    webUserData = webUserData.setLoginResponseTime(INITIAL_FAIL_LOGIN_RESPONSE_TIME);
    update("failLoginResponseTime", webUserData.failLoginResponseTime, webUserData.getSessionId());
    return webUserData;
  }

  public WebUserData incrementFailLoginResponseTime(WebUserData webUserData) throws SQLException {
    webUserData = webUserData.setLoginResponseTime(webUserData.getFailLoginResponseTime() * 2);
    update("failLoginResponseTime", webUserData.failLoginResponseTime, webUserData.getSessionId());
    return webUserData;
  }

  public WebUserData get(String sessionId) throws SQLException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, sessionId);
    }
  }

  public WebUserData get(Connection connection, String sessionId) throws SQLException {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      String query
              = "SELECT "
              + "w.id AS WebUserDataId, `failLoginResponseTime`, "
              + "cl.id AS countryLanguageId, cl.twoLettersCode AS countryLanguageTwoLettersCode, "
              + "al.id AS actualLanguageId, al.twoLettersCode AS actualLanguageTwoLettersCode, "
              + "`user` "
              + "FROM " + WebUserDataTable.DATABASE + "." + WebUserDataTable.NAME + " AS w "
              + "LEFT JOIN " + LanguagesTable.DATABASE + "." + LanguagesTable.NAME + " AS cl ON w.countryLanguage = cl.id "
              + "LEFT JOIN " + LanguagesTable.DATABASE + "." + LanguagesTable.NAME + " AS al ON w.actualLanguage = al.id "
              + "WHERE sessionId = ?";
      ps = connection.prepareStatement(query);
      ps.setString(1, sessionId);
      Logger.fine(ps);
      rs = ps.executeQuery();
      if (rs.next()) {
        Logger.fine("Client data FOUND using " + sessionId + ".");
        int id = rs.getInt("WebUserDataId");
        long failLoginResponseTime = rs.getLong("failLoginResponseTime");
        Language actualLanguage = new Language(rs.getInt("actualLanguageId"), rs.getString("actualLanguageTwoLettersCode"));
        Language countryLanguage = new Language(rs.getInt("countryLanguageId"), rs.getString("countryLanguageTwoLettersCode"));
        int userId = rs.getInt("user");
        return new WebUserData(id, sessionId, failLoginResponseTime, countryLanguage, actualLanguage, userId);
      }
      Logger.fine("Client data NOT FOUND using " + sessionId + ".");
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

  public WebUserData get(int id) throws SQLException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, id);
    }
  }

  public WebUserData get(Connection connection, int id) throws SQLException {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      String query
              = "SELECT "
              + "sessionId, `failLoginResponseTime`, "
              + "cl.id AS countryLanguageId, cl.twoLettersCode AS countryLanguageTwoLettersCode, "
              + "al.id AS actualLanguageId, al.twoLettersCode AS actualLanguageTwoLettersCode, "
              + "`user` "
              + "FROM " + WebUserDataTable.DATABASE + "." + WebUserDataTable.NAME + " AS w "
              + "LEFT JOIN " + LanguagesTable.DATABASE + "." + LanguagesTable.NAME + " AS cl ON w.countryLanguage = cl.id "
              + "LEFT JOIN " + LanguagesTable.DATABASE + "." + LanguagesTable.NAME + " AS al ON w.actualLanguage = al.id "
              + "WHERE w.id = ?";
      ps = connection.prepareStatement(query);
      ps.setInt(1, id);
      Logger.fine(ps);
      rs = ps.executeQuery();
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
        int userId = rs.getInt("user");
        return new WebUserData(id, sessionId, failLoginResponseTime, countryLanguage, actualLanguage, userId);
      }
      Logger.fine("Client data NOT FOUND using " + id + ".");
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

  private WebUserData insert(Connection connection, String sessionId) throws SQLException {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      String query
              = "INSERT INTO " + WebUserDataTable.NAME + " "
              + "(`sessionId`, `failLoginResponseTime`, `countryLanguage`, `actualLanguage`) "
              + "VALUES (?, ?, ?, ?)";
      ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, sessionId);
      // TODO take the default values from configuration database for the site
      ps.setLong(2, INITIAL_FAIL_LOGIN_RESPONSE_TIME);
      try {
        Language spanish = LanguageManager.getInstance().get("es");
        ps.setInt(3, spanish.getId());
        ps.setInt(4, spanish.getId());

        Logger.fine(ps);
        ps.executeUpdate();

        rs = ps.getGeneratedKeys();
        if (rs.next()) {
          int id = rs.getInt(1);
          // TODO Detect the langauage using the localization. IP and Web localization
          return new WebUserData(id, sessionId, INITIAL_FAIL_LOGIN_RESPONSE_TIME, spanish, spanish);
        }
      } catch (InvalidTwoLettersCodeException e) {
        throw new SofiaRuntimeException(e);
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

  public void insert(String sessionId) throws SQLException, InvalidTwoLettersCodeException {
    try (Connection connection = Database.getConnection()) {
      insert(connection, sessionId);
    }
  }

  private void update(String column, Object o, String sessionId) throws SQLException {
    PreparedStatement ps = null;
    try (Connection connection = Database.getConnection()) {
      String query = "UPDATE " + WebUserDataTable.NAME + " SET " + column + " = ? WHERE sessionId = ?";
      ps = connection.prepareStatement(query);
      ps.setObject(1, o);
      ps.setString(2, sessionId);
      Logger.fine(ps);
      ps.executeUpdate();
    } finally {
      if (ps != null) {
        ps.close();
      }
    }
  }
}
