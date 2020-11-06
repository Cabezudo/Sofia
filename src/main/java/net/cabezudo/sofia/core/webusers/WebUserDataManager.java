package net.cabezudo.sofia.core.webusers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
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

  public class WebUserData {

    private final int id;
    private final String sessionId;
    private final long failLoginResponseTime;
    private final String languageCode;
    private final String languageCountryCode;
    private Locale locale;
    private int userId;
    private User user;
    private JSONObject jsonObject;

    private WebUserData(int id, String sessionId, long failLoginResponseTime, String languageCode, String languageCountryCode) {
      this(id, sessionId, failLoginResponseTime, languageCode, languageCountryCode, 0);
    }

    private WebUserData(int id, String sessionId, long failLoginResponseTime, String languageCode, String languageCountryCode, int userId) {
      this.id = id;
      this.sessionId = sessionId;
      this.failLoginResponseTime = failLoginResponseTime;
      this.languageCode = languageCode;
      this.languageCountryCode = languageCountryCode;
      this.userId = userId;
    }

    public int getId() {
      return id;
    }

    public String getSessionId() {
      return sessionId;
    }

    public Locale getLocale() {
      if (locale == null) {
        locale = new Locale(languageCode, languageCountryCode);
      }
      return locale;
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
        JSONObject jsonLanguageObject = new JSONObject();
        jsonLanguageObject.add(new JSONPair("code", languageCode));
        jsonLanguageObject.add(new JSONPair("countryCode", languageCountryCode));
        jsonObject.add(new JSONPair("language", jsonLanguageObject));
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
      return new WebUserData(id, sessionId, failLoginResponseTime, languageCode, languageCountryCode, userId);
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
      String query = "SELECT "
              + "id, `failLoginResponseTime`, `languageCode`, `languageCountryCode`, `user` "
              + "FROM " + WebUserDataTable.NAME + " WHERE sessionId = ?";
      ps = connection.prepareStatement(query);
      ps.setString(1, sessionId);
      Logger.fine(ps);
      rs = ps.executeQuery();
      if (rs.next()) {
        Logger.fine("Client data FOUND using " + sessionId + ".");
        int id = rs.getInt("id");
        long failLoginResponseTime = rs.getLong("failLoginResponseTime");
        String languageCode = rs.getString("languageCode");
        String languageCountryCode = rs.getString("languageCountryCode");
        int userId = rs.getInt("user");
        return new WebUserData(id, sessionId, failLoginResponseTime, languageCode, languageCountryCode, userId);
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
      String query = "SELECT "
              + "sessionId, `failLoginResponseTime`, `languageCode`, `languageCountryCode`, `user` "
              + "FROM " + WebUserDataTable.DATABASE + "." + WebUserDataTable.NAME + " WHERE id = ?";
      ps = connection.prepareStatement(query);
      ps.setInt(1, id);
      Logger.fine(ps);
      rs = ps.executeQuery();
      if (rs.next()) {
        Logger.fine("Client data FOUND using " + id + ".");
        String sessionId = rs.getString("sessionId");
        long failLoginResponseTime = rs.getLong("failLoginResponseTime");
        String languageCode = rs.getString("languageCode");
        String languageCountryCode = rs.getString("languageCountryCode");
        int userId = rs.getInt("user");
        return new WebUserData(id, sessionId, failLoginResponseTime, languageCode, languageCountryCode, userId);
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
      String query = "INSERT INTO " + WebUserDataTable.NAME + " (`sessionId`, `failLoginResponseTime`, `languageCode`, `languageCountryCode`) VALUES (?, ?, ?, ?)";
      ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, sessionId);
      // TODO take the default values from configuration database for the site
      ps.setLong(2, INITIAL_FAIL_LOGIN_RESPONSE_TIME);
      ps.setString(3, "es");
      ps.setString(4, "MX");

      Logger.fine(ps);
      ps.executeUpdate();

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new WebUserData(id, sessionId, INITIAL_FAIL_LOGIN_RESPONSE_TIME, "es", "MX");
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

  public void insert(String sessionId) throws SQLException {
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
