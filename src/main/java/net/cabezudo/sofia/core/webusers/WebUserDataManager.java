package net.cabezudo.sofia.core.webusers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.database.Database;
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

  public class ClientData {

    private final String sessionId;
    private final long failLoginResponseTime;
    private final String languageCode;
    private final String languageCountryCode;
    private Locale locale;
    private int userId;
    private User user;
    private JSONObject jsonObject;

    private ClientData(String sessionId, long failLoginResponseTime, String languageCode, String languageCountryCode) {
      this(sessionId, failLoginResponseTime, languageCode, languageCountryCode, 0);
    }

    private ClientData(String sessionId, long failLoginResponseTime, String languageCode, String languageCountryCode, int userId) {
      this.sessionId = sessionId;
      this.failLoginResponseTime = failLoginResponseTime;
      this.languageCode = languageCode;
      this.languageCountryCode = languageCountryCode;
      this.userId = userId;
    }

    private ClientData(String sessionId) {
      this(sessionId, INITIAL_FAIL_LOGIN_RESPONSE_TIME, "es", "MX");
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

    private ClientData setLoginResponseTime(long failLoginResponseTime) {
      jsonObject = null;
      return new ClientData(sessionId, failLoginResponseTime, languageCode, languageCountryCode, userId);
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

  public synchronized ClientData get(HttpServletRequest request) throws SQLException {
    HttpSession session = request.getSession();
    String sessionId = session.getId();

    try ( Connection connection = Database.getConnection()) {
      connection.setAutoCommit(false);
      ClientData clientData = get(connection, sessionId);

      if (clientData == null) {
        clientData = new ClientData(sessionId);
        insert(connection, clientData);
      }
      connection.commit();
      return clientData;
    }
  }

  public ClientData resetFailLoginResponseTime(ClientData clientData) throws SQLException {
    clientData = clientData.setLoginResponseTime(INITIAL_FAIL_LOGIN_RESPONSE_TIME);
    update("failLoginResponseTime", clientData.failLoginResponseTime, clientData.getSessionId());
    return clientData;
  }

  public ClientData incrementFailLoginResponseTime(ClientData clientData) throws SQLException {
    clientData = clientData.setLoginResponseTime(clientData.getFailLoginResponseTime() * 2);
    update("failLoginResponseTime", clientData.failLoginResponseTime, clientData.getSessionId());
    return clientData;
  }

  public ClientData get(Connection connection, String sessionId) throws SQLException {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      String query = "SELECT "
              + "`failLoginResponseTime`, `languageCode`, `languageCountryCode`, `user` "
              + "FROM " + WebUserDataTable.NAME + " WHERE sessionId = ?";
      ps = connection.prepareStatement(query);
      ps.setString(1, sessionId);
      Logger.fine(ps);
      rs = ps.executeQuery();
      if (rs.next()) {
        Logger.fine("Client data FOUND using " + sessionId + ".");
        long failLoginResponseTime = rs.getLong("failLoginResponseTime");
        String languageCode = rs.getString("languageCode");
        String languageCountryCode = rs.getString("languageCountryCode");
        int userId = rs.getInt("user");
        ClientData clientData = new ClientData(sessionId, failLoginResponseTime, languageCode, languageCountryCode, userId);
        return clientData;
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

  public ClientData get(String sessionId) throws SQLException {
    try ( Connection connection = Database.getConnection()) {
      return get(connection, sessionId);
    }
  }

  private void insert(Connection connection, ClientData clientData) throws SQLException {
    PreparedStatement ps = null;
    try {
      String query = "INSERT INTO " + WebUserDataTable.NAME + " (`sessionId`, `failLoginResponseTime`, `languageCode`, `languageCountryCode`) VALUES (?, ?, ?, ?)";
      ps = connection.prepareStatement(query);
      ps.setString(1, clientData.sessionId);
      ps.setLong(2, clientData.failLoginResponseTime);
      ps.setString(3, clientData.languageCode);
      ps.setString(4, clientData.languageCountryCode);

      Logger.fine(ps);
      ps.executeUpdate();
    } finally {
      if (ps != null) {
        ps.close();
      }
    }
  }

  public void insert(ClientData clientData) throws SQLException {
    try ( Connection connection = Database.getConnection()) {
      insert(connection, clientData);
    }
  }

  private void update(String column, Object o, String sessionId) throws SQLException {
    PreparedStatement ps = null;
    try ( Connection connection = Database.getConnection()) {
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
