package net.cabezudo.sofia.core.webusers;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.currency.Currency;
import net.cabezudo.sofia.core.languages.InvalidTwoLettersCodeException;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.languages.LanguageManager;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserManager;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.02.23
 */
public class WebUserData {

  private final int id;
  private final String sessionId;
  private long failLoginResponseTime;
  private Language countryLanguage;
  private Language actualLanguage;
  private Currency actualCurrency;
  private int userId;
  private User user;
  private JSONObject jsonObject;

  public WebUserData(int id, String sessionId, long failLoginResponseTime, Language countryLanguage, Language actualLanguage, Currency actualCurrency) {
    this(id, sessionId, failLoginResponseTime, countryLanguage, actualLanguage, actualCurrency, 0);
  }

  public WebUserData(int id, String sessionId, long failLoginResponseTime, Language countryLanguage, Language actualLanguage, Currency actualCurrency, int userId) {
    this.id = id;
    this.sessionId = sessionId;
    this.failLoginResponseTime = failLoginResponseTime;
    this.countryLanguage = countryLanguage;
    this.actualLanguage = actualLanguage;
    this.actualCurrency = actualCurrency;
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

  public void setActualLanguage(String twoLetterCodeLanguage) throws InvalidTwoLettersCodeException, ClusterException {
    Language language = LanguageManager.getInstance().get(twoLetterCodeLanguage);
    actualLanguage = language;
  }

  public Language getActualLanguage() {
    return actualLanguage;
  }

  public Currency getActualCurrency() {
    return actualCurrency;
  }

  public void resetFailLoginResponseTime() {
    this.failLoginResponseTime = 0;
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
      jsonObject.add(new JSONPair("actualCurrency", actualCurrency.getCurrencyCode()));
      jsonObject.add(new JSONPair("logged", isLogged()));
      try {
        user = getUser();
      } catch (ClusterException e) {
        Logger.warning(e);
        user = null;
      }
      jsonObject.add(new JSONPair("user", user == null ? user : user.toString()));
    }
    return jsonObject.toString();
  }

  public WebUserData setLoginResponseTime(long failLoginResponseTime) {
    jsonObject = null;
    return new WebUserData(id, sessionId, failLoginResponseTime, countryLanguage, actualLanguage, actualCurrency, userId);
  }

  public void setUser(User user) {
    if (user == null) {
      this.user = null;
      this.userId = 0;
    } else {
      this.user = user;
      this.userId = user.getId();
    }
  }

  public User getUser() throws ClusterException {
    Logger.debug("user id = %s, user = %s", userId, user);
    if (userId != 0 && user == null) {
      user = UserManager.getInstance().get(userId);
    }
    return user;
  }
}
